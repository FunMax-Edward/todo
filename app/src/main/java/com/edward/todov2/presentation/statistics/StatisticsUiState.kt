package com.edward.todov2.presentation.statistics

import java.util.Calendar

/**
 * UI State for Statistics Screen
 */
data class StatisticsUiState(
    val heatmapData: List<DayActivity> = emptyList(),
    val totalDone: Int = 0,
    val accuracy: Int = 0,
    val lastActiveDate: String = "无",
    val isLoading: Boolean = false,
    // 日历相关
    val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH), // 0-based
    val calendarDays: List<CalendarDay> = emptyList(),
    val allActivityData: Map<String, Int> = emptyMap() // date -> count
)

data class DayActivity(
    val date: String,
    val count: Int,
    val color: androidx.compose.ui.graphics.Color
)

data class CalendarDay(
    val dayOfMonth: Int, // 0 表示空白占位
    val date: String,    // yyyy-MM-dd
    val count: Int,
    val isCurrentMonth: Boolean = true
)

