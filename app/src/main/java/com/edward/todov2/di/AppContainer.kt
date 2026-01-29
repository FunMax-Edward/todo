package com.edward.todov2.di

import android.app.Application
import com.edward.todov2.data.AppDatabase
import com.edward.todov2.domain.repository.StudyRepository
import com.edward.todov2.domain.usecase.UpdateProblemProficiencyUseCase
import com.edward.todov2.presentation.problemgrid.ProblemLabelFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Simple DI container for the app
 * In a larger app, consider using Hilt or Koin
 */
object AppContainer {
    private var _repository: StudyRepository? = null
    private var _updateProficiencyUseCase: UpdateProblemProficiencyUseCase? = null
    
    // Shared label format state
    private val _labelFormat = MutableStateFlow(ProblemLabelFormat.UNIT_DASH)
    val labelFormat: StateFlow<ProblemLabelFormat> = _labelFormat.asStateFlow()

    fun updateLabelFormat(format: ProblemLabelFormat) {
        _labelFormat.value = format
    }

    fun init(application: Application) {
        val dao = AppDatabase.getDatabase(application).studyDao()
        _repository = StudyRepository(dao)
        _updateProficiencyUseCase = UpdateProblemProficiencyUseCase(_repository!!)
    }
    
    val repository: StudyRepository
        get() = _repository ?: throw IllegalStateException("AppContainer not initialized")
    
    val updateProficiencyUseCase: UpdateProblemProficiencyUseCase
        get() = _updateProficiencyUseCase ?: throw IllegalStateException("AppContainer not initialized")
}
