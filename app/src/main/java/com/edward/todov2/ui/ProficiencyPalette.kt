package com.edward.todov2.ui

import androidx.compose.ui.graphics.Color

object ProficiencyPalette {
    private val palette = listOf(
        Color(0xFFE5E7EB), // Level 0 - grey
        Color(0xFFFFB4B4), // Level 1 - light red
        Color(0xFFFF8B8B), // Level 2 - medium red
        Color(0xFFE34D4D), // Level 3 - deep red
        Color(0xFFB91C1C), // Level 4 - darkest red
        Color(0xFF4CAF50)  // Level 5 - mastered green
    )

    fun colorFor(level: Int): Color {
        return when (level) {
            in 0..5 -> palette[level]
            else -> Color.Gray
        }
    }

    fun labelFor(level: Int): String = when (level) {
        0 -> "从未做过"
        1 -> "不太稳"
        2 -> "不熟练"
        3 -> "较生疏"
        4 -> "重灾区"
        5 -> "已掌握"
        else -> "未知"
    }
}
