package com.edward.todov2.presentation.unitlist

import com.edward.todov2.data.Project
import com.edward.todov2.data.StudyUnit

/**
 * UI State for Unit List Screen (Home)
 */
data class UnitListUiState(
    // 当前激活的题集
    val activeProject: Project? = null,
    // 所有题集列表
    val allProjects: List<ProjectUiModel> = emptyList(),
    // 当前激活题集的单元列表
    val units: List<UnitItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val showProjectSelector: Boolean = false,
    val errorMessage: String? = null
)

/**
 * 题集 UI 模型
 */
data class ProjectUiModel(
    val project: Project,
    val unitCount: Int,
    val totalProblems: Int,
    val masteredCount: Int
)

/**
 * 单元 UI 模型
 */
data class UnitItemUiModel(
    val unit: StudyUnit,
    val totalProblems: Int,
    val masteredCount: Int,
    val progressPercentage: Float,
    val levelDistribution: Map<Int, Int> // level -> count
)

/**
 * User actions
 */
sealed interface UnitListAction {
    // 单元操作
    data class UnitClicked(val unitId: Int) : UnitListAction
    data class DeleteUnit(val unitId: Int) : UnitListAction

    // 题集操作
    data class ActivateProject(val projectId: Int) : UnitListAction
    data class DeleteProject(val projectId: Int) : UnitListAction
    data object ShowProjectSelector : UnitListAction
    data object DismissProjectSelector : UnitListAction
}
