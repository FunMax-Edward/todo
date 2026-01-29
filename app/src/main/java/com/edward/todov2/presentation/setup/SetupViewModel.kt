package com.edward.todov2.presentation.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edward.todov2.data.Project
import com.edward.todov2.domain.repository.StudyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * 题集创建页面的 UI 状态
 */
data class SetupUiState(
    val projectName: String = "",
    val unitDefinitions: String = "",
    val isCreating: Boolean = false,
    val createSuccess: Boolean = false,
    val errorMessage: String? = null,
    // 已有的题集列表（用于管理）
    val existingProjects: List<ProjectManageModel> = emptyList()
)

data class ProjectManageModel(
    val project: Project,
    val unitCount: Int,
    val totalProblems: Int,
    val masteredCount: Int
)

/**
 * 用户操作
 */
sealed interface SetupAction {
    data class UpdateProjectName(val name: String) : SetupAction
    data class UpdateUnitDefinitions(val definitions: String) : SetupAction
    data object CreateProject : SetupAction
    data class DeleteProject(val projectId: Int) : SetupAction
    data object ClearError : SetupAction
    data object ResetForm : SetupAction
}

class SetupViewModel(
    private val repository: StudyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    init {
        loadExistingProjects()
    }

    private fun loadExistingProjects() {
        viewModelScope.launch {
            combine(
                repository.getAllProjects(),
                repository.getAllUnits(),
                repository.getAllProblems()
            ) { projects, units, problems ->
                projects.map { project ->
                    val projectUnits = units.filter { it.projectId == project.id }
                    val projectProblems = problems.filter { problem ->
                        projectUnits.any { it.id == problem.unitId }
                    }
                    ProjectManageModel(
                        project = project,
                        unitCount = projectUnits.size,
                        totalProblems = projectProblems.size,
                        masteredCount = projectProblems.count { it.level == 5 }
                    )
                }
            }.collect { projectModels ->
                _uiState.update { it.copy(existingProjects = projectModels) }
            }
        }
    }

    fun onAction(action: SetupAction) {
        when (action) {
            is SetupAction.UpdateProjectName -> {
                _uiState.update { it.copy(projectName = action.name) }
            }
            is SetupAction.UpdateUnitDefinitions -> {
                _uiState.update { it.copy(unitDefinitions = action.definitions) }
            }
            SetupAction.CreateProject -> {
                createProject()
            }
            is SetupAction.DeleteProject -> {
                deleteProject(action.projectId)
            }
            SetupAction.ClearError -> {
                _uiState.update { it.copy(errorMessage = null) }
            }
            SetupAction.ResetForm -> {
                _uiState.update {
                    it.copy(
                        projectName = "",
                        unitDefinitions = "",
                        createSuccess = false
                    )
                }
            }
        }
    }

    private fun createProject() {
        val currentState = _uiState.value

        // 验证输入
        if (currentState.projectName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "请输入题集名称") }
            return
        }

        val unitDefs = parseUnitDefinitions(currentState.unitDefinitions)
        if (unitDefs.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "请输入有效的单元定义，格式：U1: 32") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isCreating = true, errorMessage = null) }

            try {
                repository.createProjectWithUnits(
                    projectName = currentState.projectName.trim(),
                    unitDefinitions = unitDefs,
                    autoActivate = true
                )

                _uiState.update {
                    it.copy(
                        isCreating = false,
                        createSuccess = true,
                        projectName = "",
                        unitDefinitions = ""
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isCreating = false,
                        errorMessage = "创建失败: ${e.message}"
                    )
                }
            }
        }
    }

    private fun deleteProject(projectId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteProject(projectId)
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "删除失败: ${e.message}") }
            }
        }
    }

    /**
     * 解析用户输入的单元定义
     * 支持格式：
     * - "U1: 32" 或 "U1:32"
     * - "第一章: 25"
     * - "Unit1 20"
     * 每行一个单元
     */
    private fun parseUnitDefinitions(input: String): List<Pair<String, Int>> {
        val result = mutableListOf<Pair<String, Int>>()

        // 支持多种分隔符：换行、逗号、分号
        val lines = input.trim().split(Regex("[\\n,;]+"))

        for (line in lines) {
            val trimmedLine = line.trim()
            if (trimmedLine.isBlank()) continue

            // 尝试匹配 "Name: 20" 或 "Name:20" (Name可包含字母数字)
            val colonRegex = Regex("(.+?)[:\\s]+(\\d+)\\s*$")
            val colonMatch = colonRegex.find(trimmedLine)

            if (colonMatch != null) {
                val name = colonMatch.groupValues[1].trim()
                val count = colonMatch.groupValues[2].toIntOrNull() ?: 0
                if (name.isNotBlank() && count > 0) {
                    result.add(name to count)
                    continue
                }
            }

            // 尝试匹配 "Name 20"（最后一个是数字）
            val spaceRegex = Regex("(.+?)\\s+(\\d+)\\s*$")
            val spaceMatch = spaceRegex.find(trimmedLine)

            if (spaceMatch != null) {
                val name = spaceMatch.groupValues[1].trim()
                val count = spaceMatch.groupValues[2].toIntOrNull() ?: 0
                if (name.isNotBlank() && count > 0) {
                    result.add(name to count)
                }
            }
        }

        return result
    }
}
