package com.edward.todov2.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.edward.todov2.di.AppContainer
import com.edward.todov2.presentation.problemgrid.ProblemGridViewModel
import com.edward.todov2.presentation.settings.SettingsViewModel
import com.edward.todov2.presentation.setup.SetupViewModel
import com.edward.todov2.presentation.statistics.StatisticsViewModel
import com.edward.todov2.presentation.unitlist.UnitListViewModel

/**
 * Factory for creating ViewModels with dependencies
 */
class ViewModelFactory(
    private val unitId: Int? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UnitListViewModel::class.java) -> {
                UnitListViewModel(
                    repository = AppContainer.repository
                ) as T
            }
            modelClass.isAssignableFrom(SetupViewModel::class.java) -> {
                SetupViewModel(
                    repository = AppContainer.repository
                ) as T
            }
            modelClass.isAssignableFrom(StatisticsViewModel::class.java) -> {
                StatisticsViewModel(
                    repository = AppContainer.repository
                ) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(
                    repository = AppContainer.repository
                ) as T
            }
            modelClass.isAssignableFrom(ProblemGridViewModel::class.java) -> {
                require(unitId != null) { "unitId is required for ProblemGridViewModel" }
                ProblemGridViewModel(
                    unitId = unitId,
                    repository = AppContainer.repository,
                    updateProficiencyUseCase = AppContainer.updateProficiencyUseCase,
                    labelFormat = AppContainer.labelFormat
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
