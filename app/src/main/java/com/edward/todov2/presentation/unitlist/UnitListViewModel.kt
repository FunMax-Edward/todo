package com.edward.todov2.presentation.unitlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edward.todov2.domain.repository.StudyRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class UnitListViewModel(
    private val repository: StudyRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(UnitListUiState())
    val uiState: StateFlow<UnitListUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            // 监听所有题集
            combine(
                repository.getActiveProject(),
                repository.getAllProjects(),
                repository.getAllProblems()
            ) { activeProject, allProjects, allProblems ->
                Triple(activeProject, allProjects, allProblems)
            }.flatMapLatest { (activeProject, allProjects, allProblems) ->
                // 如果有激活的题集，加载其单元
                val unitsFlow = if (activeProject != null) {
                    repository.getUnitsForProject(activeProject.id)
                } else {
                    flowOf(emptyList())
                }

                unitsFlow.map { units ->
                    // 构建题集列表 UI 模型
                    val projectModels = allProjects.map { project ->
                        val projectUnits = units.filter { it.projectId == project.id }
                        val projectProblems = allProblems.filter { problem ->
                            projectUnits.any { it.id == problem.unitId }
                        }
                        ProjectUiModel(
                            project = project,
                            unitCount = projectUnits.size,
                            totalProblems = projectProblems.size,
                            masteredCount = projectProblems.count { it.level == 5 }
                        )
                    }

                    // 构建单元列表 UI 模型
                    val unitModels = units.map { unit ->
                        val unitProblems = allProblems.filter { it.unitId == unit.id }
                        val levelDist = unitProblems.groupingBy { it.level }.eachCount()
                        val mastered = levelDist[5] ?: 0
                        val total = unitProblems.size
                        val progress = if (total > 0) mastered.toFloat() / total else 0f

                        UnitItemUiModel(
                            unit = unit,
                            totalProblems = total,
                            masteredCount = mastered,
                            progressPercentage = progress,
                            levelDistribution = levelDist
                        )
                    }

                    UnitListUiState(
                        activeProject = activeProject,
                        allProjects = projectModels,
                        units = unitModels,
                        isLoading = false
                    )
                }
            }.collect { state ->
                _uiState.value = state
            }
        }
    }
    
    fun onAction(action: UnitListAction) {
        when (action) {
            is UnitListAction.UnitClicked -> {
                // Handled by screen navigation
            }
            is UnitListAction.DeleteUnit -> {
                deleteUnit(action.unitId)
            }
            is UnitListAction.ActivateProject -> {
                activateProject(action.projectId)
            }
            is UnitListAction.DeleteProject -> {
                deleteProject(action.projectId)
            }
            UnitListAction.ShowProjectSelector -> {
                _uiState.update { it.copy(showProjectSelector = true) }
            }
            UnitListAction.DismissProjectSelector -> {
                _uiState.update { it.copy(showProjectSelector = false) }
            }
        }
    }
    
    private fun deleteUnit(unitId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteUnit(unitId)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "删除失败: ${e.message}")
                }
            }
        }
    }
    
    private fun activateProject(projectId: Int) {
        viewModelScope.launch {
            try {
                repository.activateProject(projectId)
                _uiState.update { it.copy(showProjectSelector = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "激活失败: ${e.message}")
                }
            }
        }
    }

    private fun deleteProject(projectId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteProject(projectId)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = "删除失败: ${e.message}")
                }
            }
        }
    }
}
