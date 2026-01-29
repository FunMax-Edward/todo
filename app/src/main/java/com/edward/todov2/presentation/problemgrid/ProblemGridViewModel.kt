package com.edward.todov2.presentation.problemgrid

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edward.todov2.data.Problem
import com.edward.todov2.domain.repository.StudyRepository
import com.edward.todov2.domain.usecase.UpdateProblemProficiencyUseCase
import com.edward.todov2.ui.ProficiencyPalette
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Modern ViewModel for Problem Grid with proper state management
 */
class ProblemGridViewModel(
    private val unitId: Int,
    private val repository: StudyRepository,
    private val updateProficiencyUseCase: UpdateProblemProficiencyUseCase,
    private val labelFormat: StateFlow<ProblemLabelFormat>
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProblemGridUiState())
    val uiState: StateFlow<ProblemGridUiState> = _uiState.asStateFlow()
    
    init {
        loadProblems()
    }
    
    private fun loadProblems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            combine(
                repository.getUnitById(unitId),
                repository.getProblemsForUnit(unitId),
                labelFormat
            ) { unit, problems, format ->
                Triple(unit, problems, format)
            }.collect { (unit, problems, format) ->
                _uiState.update {
                    it.copy(
                        unitName = unit?.name ?: "Unit",
                        problems = problems.map { problem ->
                            problem.toUiModel(unit?.name ?: "Unit", format)
                        },
                        isLoading = false
                    )
                }
            }
        }
    }
    
    fun onAction(action: ProblemGridAction) {
        when (action) {
            is ProblemGridAction.ProblemClicked -> {
                _uiState.update { it.copy(selectedProblem = action.problem) }
            }
            is ProblemGridAction.ProblemLongPressed -> {
                _uiState.update { it.copy(detailProblem = action.problem) }
            }
            is ProblemGridAction.MarkResult -> {
                handleMarkResult(action.problem, action.isCorrect)
            }
            ProblemGridAction.DismissDialog -> {
                _uiState.update { it.copy(selectedProblem = null, detailProblem = null) }
            }
            ProblemGridAction.NavigateBack -> {
                // Handled by screen
            }
        }
    }
    
    private fun handleMarkResult(problemUi: ProblemUiModel, isCorrect: Boolean) {
        viewModelScope.launch {
            try {
                // Trigger animation
                _uiState.update { state ->
                    state.copy(
                        problems = state.problems.map { 
                            if (it.problem.id == problemUi.problem.id) {
                                it.copy(isAnimating = true)
                            } else it
                        }
                    )
                }
                
                updateProficiencyUseCase(problemUi.problem, isCorrect)
                
                _uiState.update { it.copy(selectedProblem = null) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        errorMessage = "Failed to update problem: ${e.message}",
                        selectedProblem = null
                    )
                }
            }
        }
    }
    
    private fun Problem.toUiModel(unitName: String, format: ProblemLabelFormat): ProblemUiModel {
        val label = when (format) {
            ProblemLabelFormat.UNIT_DASH -> "$unitName-$problemIndex"
            ProblemLabelFormat.DECIMAL -> "$unitId.$problemIndex"
            ProblemLabelFormat.HASH -> "$unitName#$problemIndex"
        }
        
        val bgColor = ProficiencyPalette.colorFor(level)
        val textColor = if (level == 0) Color.DarkGray else Color.White
        
        return ProblemUiModel(
            problem = this,
            label = label,
            backgroundColor = bgColor,
            textColor = textColor,
            levelLabel = ProficiencyPalette.labelFor(level)
        )
    }
}

enum class ProblemLabelFormat(val displayName: String) {
    UNIT_DASH("U1-12"),
    DECIMAL("1.12"),
    HASH("Unit1#12")
}
