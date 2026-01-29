package com.edward.todov2.presentation.common

/**
 * Generic UI state wrapper for async operations
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
    data object Empty : UiState<Nothing>
}

/**
 * UI Event - one-time events that should not survive configuration changes
 */
sealed interface UiEvent {
    data class ShowSnackbar(val message: String) : UiEvent
    data class Navigate(val route: String) : UiEvent
    data object NavigateBack : UiEvent
}
