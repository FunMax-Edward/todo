package com.edward.todov2.presentation.statistics

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edward.todov2.domain.repository.StudyRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class StatisticsViewModel(
    private val repository: StudyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init {
        loadStatistics()
    }

    fun previousMonth() {
        val current = _uiState.value
        val calendar = Calendar.getInstance()
        calendar.set(current.currentYear, current.currentMonth, 1)
        calendar.add(Calendar.MONTH, -1)

        _uiState.update {
            it.copy(
                currentYear = calendar.get(Calendar.YEAR),
                currentMonth = calendar.get(Calendar.MONTH)
            )
        }
        updateCalendarDays()
    }

    fun nextMonth() {
        val current = _uiState.value
        val calendar = Calendar.getInstance()
        calendar.set(current.currentYear, current.currentMonth, 1)
        calendar.add(Calendar.MONTH, 1)

        _uiState.update {
            it.copy(
                currentYear = calendar.get(Calendar.YEAR),
                currentMonth = calendar.get(Calendar.MONTH)
            )
        }
        updateCalendarDays()
    }

    private fun updateCalendarDays() {
        val current = _uiState.value
        val calendarDays = buildCalendarDays(
            current.currentYear,
            current.currentMonth,
            current.allActivityData
        )
        _uiState.update { it.copy(calendarDays = calendarDays) }
    }

    private fun loadStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            repository.getAllLogs().collect { logs ->
                val logsByDay = logs.groupBy {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.date))
                }

                // 统计每天的练习次数
                val activityData = logsByDay.mapValues { it.value.size }

                // 旧的热力图数据（保留兼容）
                val days = (0..34).map { i ->
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.DAY_OF_YEAR, -(34 - i))
                    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
                    val count = logsByDay[dateStr]?.size ?: 0
                    
                    DayActivity(
                        date = dateStr,
                        count = count,
                        color = getHeatmapColor(count)
                    )
                }

                val totalDone = logs.size
                val correctCount = logs.count { it.isCorrect }
                val accuracy = if (totalDone > 0) (correctCount.toFloat() / totalDone * 100).toInt() else 0
                val lastDate = if (logs.isNotEmpty()) {
                    SimpleDateFormat("MM-dd", Locale.getDefault()).format(Date(logs.last().date))
                } else "无"

                val currentState = _uiState.value
                val calendarDays = buildCalendarDays(
                    currentState.currentYear,
                    currentState.currentMonth,
                    activityData
                )

                _uiState.update {
                    it.copy(
                        heatmapData = days,
                        totalDone = totalDone,
                        accuracy = accuracy,
                        lastActiveDate = lastDate,
                        isLoading = false,
                        allActivityData = activityData,
                        calendarDays = calendarDays
                    )
                }
            }
        }
    }

    private fun buildCalendarDays(year: Int, month: Int, activityData: Map<String, Int>): List<CalendarDay> {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, 1)

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0=Sunday

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val result = mutableListOf<CalendarDay>()

        // 添加空白占位（月初之前的空格）
        repeat(firstDayOfWeek) {
            result.add(CalendarDay(dayOfMonth = 0, date = "", count = 0, isCurrentMonth = false))
        }

        // 添加当月每一天
        for (day in 1..daysInMonth) {
            calendar.set(year, month, day)
            val dateStr = dateFormat.format(calendar.time)
            val count = activityData[dateStr] ?: 0
            result.add(CalendarDay(dayOfMonth = day, date = dateStr, count = count))
        }

        return result
    }

    private fun getHeatmapColor(count: Int): Color {
        return when {
            count == 0 -> Color(0xFFF3F4F6)
            count < 5 -> Color(0xFFDCFCE7)
            count < 10 -> Color(0xFF86EFAC)
            count < 20 -> Color(0xFF22C55E)
            else -> Color(0xFF15803D)
        }
    }
}
