package com.edward.todov2.presentation.problemgrid

import com.edward.todov2.data.Problem

/**
 * UI State for Problem Grid Screen
 */
data class ProblemGridUiState(
    val unitName: String = "",
    val problems: List<ProblemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val selectedProblem: ProblemUiModel? = null,
    val detailProblem: ProblemUiModel? = null,
    val errorMessage: String? = null
)

data class ProblemUiModel(
    val problem: Problem,
    val label: String,
    val backgroundColor: androidx.compose.ui.graphics.Color,
    val textColor: androidx.compose.ui.graphics.Color,
    val levelLabel: String,
    val isAnimating: Boolean = false
)

/**
 * User actions from the UI
 */
sealed interface ProblemGridAction {
    data class ProblemClicked(val problem: ProblemUiModel) : ProblemGridAction
    data class ProblemLongPressed(val problem: ProblemUiModel) : ProblemGridAction
    data class MarkResult(val problem: ProblemUiModel, val isCorrect: Boolean) : ProblemGridAction
    data object DismissDialog : ProblemGridAction
    data object NavigateBack : ProblemGridAction
}
