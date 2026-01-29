package com.edward.todov2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 题集/工程 - 顶层容器
 * 例如：高数1000题、线代习题集
 */
@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val isActive: Boolean = false,  // 是否为当前激活的题集
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 单元 - 属于某个题集
 * 例如：U1、U2、第一章
 */
@Entity(tableName = "units")
data class StudyUnit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val projectId: Int,  // 关联到 Project
    val name: String,
    val problemCount: Int,
    val sortOrder: Int = 0  // 用于排序
)

/**
 * 题目 - 属于某个单元
 */
@Entity(tableName = "problems")
data class Problem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val unitId: Int,
    val problemIndex: Int, // 1, 2, 3...
    val level: Int = 0, // 0-5
    val correctCount: Int = 0,
    val wrongCount: Int = 0
)

/**
 * 活动日志 - 记录每次练习
 */
@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long, // timestamp
    val problemId: Int,
    val isCorrect: Boolean
)
