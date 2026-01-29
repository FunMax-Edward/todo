package com.edward.todov2.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    // ==================== Project 相关 ====================
    @Query("SELECT * FROM projects ORDER BY createdAt DESC")
    fun getAllProjects(): Flow<List<Project>>

    @Query("SELECT * FROM projects WHERE isActive = 1 LIMIT 1")
    fun getActiveProject(): Flow<Project?>

    @Insert
    suspend fun insertProject(project: Project): Long

    @Query("UPDATE projects SET isActive = 0")
    suspend fun deactivateAllProjects()

    @Query("UPDATE projects SET isActive = 1 WHERE id = :projectId")
    suspend fun activateProject(projectId: Int)

    @Query("DELETE FROM projects WHERE id = :projectId")
    suspend fun deleteProject(projectId: Int): Int

    // ==================== Unit 相关 ====================
    @Query("SELECT * FROM units")
    fun getAllUnits(): Flow<List<StudyUnit>>

    @Query("SELECT * FROM units WHERE projectId = :projectId ORDER BY sortOrder ASC")
    fun getUnitsForProject(projectId: Int): Flow<List<StudyUnit>>

    @Insert
    suspend fun insertUnit(unit: StudyUnit): Long

    @Insert
    suspend fun insertUnits(units: List<StudyUnit>): List<Long>

    @Query("SELECT * FROM problems WHERE unitId = :unitId ORDER BY problemIndex ASC")
    fun getProblemsForUnit(unitId: Int): Flow<List<Problem>>

    @Insert
    suspend fun insertProblems(problems: List<Problem>): List<Long>

    @Update
    suspend fun updateProblem(problem: Problem): Int

    @Query("SELECT * FROM problems WHERE id = :problemId")
    suspend fun getProblemById(problemId: Int): Problem?

    @Query("DELETE FROM units WHERE id = :unitId")
    suspend fun deleteUnit(unitId: Int): Int

    @Query("DELETE FROM units WHERE projectId = :projectId")
    suspend fun deleteUnitsForProject(projectId: Int): Int

    @Query("DELETE FROM problems WHERE unitId = :unitId")
    suspend fun deleteProblemsForUnit(unitId: Int): Int

    @Query("DELETE FROM problems WHERE unitId IN (SELECT id FROM units WHERE projectId = :projectId)")
    suspend fun deleteProblemsForProject(projectId: Int): Int

    @Query("SELECT * FROM problems")
    fun getAllProblems(): Flow<List<Problem>>

    @Query("DELETE FROM units")
    suspend fun deleteAllUnits(): Int

    @Query("DELETE FROM problems")
    suspend fun deleteAllProblems(): Int

    @Query("DELETE FROM activity_logs")
    suspend fun deleteAllLogs(): Int

    @Query("DELETE FROM projects")
    suspend fun deleteAllProjects(): Int

    @Insert
    suspend fun insertLog(log: ActivityLog): Long

    @Query("SELECT * FROM activity_logs")
    fun getAllLogs(): Flow<List<ActivityLog>>
}

// 数据库迁移：版本1到版本2，添加 projects 表和修改 units 表
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 创建 projects 表
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS projects (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                isActive INTEGER NOT NULL DEFAULT 0,
                createdAt INTEGER NOT NULL DEFAULT 0
            )
        """)

        // 创建新的 units 表（带 projectId）
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS units_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                projectId INTEGER NOT NULL DEFAULT 0,
                name TEXT NOT NULL,
                problemCount INTEGER NOT NULL,
                sortOrder INTEGER NOT NULL DEFAULT 0
            )
        """)

        // 复制旧数据到新表
        db.execSQL("INSERT INTO units_new (id, projectId, name, problemCount, sortOrder) SELECT id, 0, name, problemCount, 0 FROM units")

        // 删除旧表
        db.execSQL("DROP TABLE units")

        // 重命名新表
        db.execSQL("ALTER TABLE units_new RENAME TO units")
    }
}

@Database(entities = [Project::class, StudyUnit::class, Problem::class, ActivityLog::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studyDao(): StudyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "study_database"
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration(dropAllTables = true)  // 如果迁移失败，重建数据库
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
