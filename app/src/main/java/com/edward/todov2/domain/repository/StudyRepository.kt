package com.edward.todov2.domain.repository

import com.edward.todov2.data.ActivityLog
import com.edward.todov2.data.Problem
import com.edward.todov2.data.Project
import com.edward.todov2.data.StudyDao
import com.edward.todov2.data.StudyUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository pattern - single source of truth for study data
 * Abstracts data layer from ViewModels
 */
class StudyRepository(private val dao: StudyDao) {
    
    // ==================== Projects ====================
    fun getAllProjects(): Flow<List<Project>> = dao.getAllProjects()

    fun getActiveProject(): Flow<Project?> = dao.getActiveProject()

    suspend fun activateProject(projectId: Int) {
        dao.deactivateAllProjects()
        dao.activateProject(projectId)
    }

    suspend fun deleteProject(projectId: Int) {
        // 删除项目下所有题目
        dao.deleteProblemsForProject(projectId)
        // 删除项目下所有单元
        dao.deleteUnitsForProject(projectId)
        // 删除项目
        dao.deleteProject(projectId)
    }

    /**
     * 一次性创建完整的题集
     * @param projectName 题集名称，如"高数1000题"
     * @param unitDefinitions 单元定义列表，每个 Pair 是 (单元名, 题目数量)
     * @param autoActivate 是否自动激活该题集
     * @return 新创建的项目 ID
     */
    suspend fun createProjectWithUnits(
        projectName: String,
        unitDefinitions: List<Pair<String, Int>>,
        autoActivate: Boolean = true
    ): Long {
        // 1. 创建 Project
        val projectId = dao.insertProject(Project(name = projectName))

        // 2. 为每个单元创建 Unit 和对应的 Problems
        unitDefinitions.forEachIndexed { index, (unitName, problemCount) ->
            val unitId = dao.insertUnit(
                StudyUnit(
                    projectId = projectId.toInt(),
                    name = unitName,
                    problemCount = problemCount,
                    sortOrder = index
                )
            )

            // 为该单元生成所有题目
            val problems = (1..problemCount).map { problemIndex ->
                Problem(unitId = unitId.toInt(), problemIndex = problemIndex)
            }
            dao.insertProblems(problems)
        }

        // 3. 如果需要自动激活
        if (autoActivate) {
            dao.deactivateAllProjects()
            dao.activateProject(projectId.toInt())
        }

        return projectId
    }

    // ==================== Units ====================
    fun getAllUnits(): Flow<List<StudyUnit>> = dao.getAllUnits()
    
    fun getUnitsForProject(projectId: Int): Flow<List<StudyUnit>> =
        dao.getUnitsForProject(projectId)

    fun getUnitById(unitId: Int): Flow<StudyUnit?> {
        return dao.getAllUnits().map { units ->
            units.find { it.id == unitId }
        }
    }
    
    suspend fun insertUnit(unit: StudyUnit): Long {
        return dao.insertUnit(unit)
    }
    
    suspend fun deleteUnit(unitId: Int) {
        dao.deleteProblemsForUnit(unitId)
        dao.deleteUnit(unitId)
    }
    
    // ==================== Problems ====================
    fun getAllProblems(): Flow<List<Problem>> = dao.getAllProblems()
    
    fun getProblemsForUnit(unitId: Int): Flow<List<Problem>> = 
        dao.getProblemsForUnit(unitId)
    
    suspend fun insertProblems(problems: List<Problem>) {
        dao.insertProblems(problems)
    }
    
    suspend fun updateProblem(problem: Problem) {
        dao.updateProblem(problem)
    }
    
    // ==================== Activity Logs ====================
    fun getAllLogs(): Flow<List<ActivityLog>> = dao.getAllLogs()
    
    suspend fun insertLog(log: ActivityLog) {
        dao.insertLog(log)
    }
    
    // ==================== Legacy support (旧版兼容) ====================
    @Deprecated("Use createProjectWithUnits instead")
    suspend fun createUnitWithProblems(name: String, problemCount: Int): Long {
        // 获取或创建默认项目
        val unitId = dao.insertUnit(StudyUnit(projectId = 0, name = name, problemCount = problemCount))
        val problems = (1..problemCount).map { index ->
            Problem(unitId = unitId.toInt(), problemIndex = index)
        }
        dao.insertProblems(problems)
        return unitId
    }
    
    suspend fun clearAllData() {
        dao.deleteAllLogs()
        dao.deleteAllProblems()
        dao.deleteAllUnits()
        dao.deleteAllProjects()
    }
}
