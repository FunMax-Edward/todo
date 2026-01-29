package com.edward.todov2.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edward.todov2.di.AppContainer
import com.edward.todov2.domain.repository.StudyRepository
import com.edward.todov2.presentation.problemgrid.ProblemLabelFormat
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: StudyRepository
) : ViewModel() {

    // Use shared labelFormat from AppContainer
    val labelFormat: StateFlow<ProblemLabelFormat> = AppContainer.labelFormat

    fun updateLabelFormat(format: ProblemLabelFormat) {
        AppContainer.updateLabelFormat(format)
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }
}
