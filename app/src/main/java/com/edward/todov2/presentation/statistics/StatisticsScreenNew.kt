package com.edward.todov2.presentation.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edward.todov2.ui.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    AppScaffold(title = "学习统计") { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AppDimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(AppDimens.ItemSpacing),
            contentPadding = PaddingValues(
                top = AppDimens.ItemSpacing,
                bottom = 100.dp
            )
        ) {
            // 统计卡片
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.ItemSpacing)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "总练习",
                        value = uiState.totalDone.toString(),
                        subValue = "次",
                        color = AppColors.Primary
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        label = "正确率",
                        value = "${uiState.accuracy}%",
                        subValue = "平均",
                        color = AppColors.Success
                    )
                }
            }

            // 日历热力图
            item {
                CalendarHeatmap(
                    year = uiState.currentYear,
                    month = uiState.currentMonth,
                    calendarDays = uiState.calendarDays,
                    onPreviousMonth = { viewModel.previousMonth() },
                    onNextMonth = { viewModel.nextMonth() }
                )
            }

            // 最近活动
            item {
                AppCard {
                    Text(
                        "学习提示",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (uiState.lastActiveDate.isNotEmpty()) {
                        Text(
                            "上次练习：${uiState.lastActiveDate}",
                            fontSize = 14.sp,
                            color = AppColors.TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "💡 坚持每天练习，效果更好！",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarHeatmap(
    year: Int,
    month: Int,
    calendarDays: List<CalendarDay>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val monthNames = listOf("一月", "二月", "三月", "四月", "五月", "六月",
                            "七月", "八月", "九月", "十月", "十一月", "十二月")
    val weekDays = listOf("日", "一", "二", "三", "四", "五", "六")

    AppCard {
        // 月份切换头部
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPreviousMonth) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "上个月",
                    tint = AppColors.TextPrimary
                )
            }

            Text(
                "${year}年 ${monthNames[month]}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextPrimary
            )

            IconButton(onClick = onNextMonth) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "下个月",
                    tint = AppColors.TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 星期标题行
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            weekDays.forEach { day ->
                Text(
                    text = day,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.TextHint,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 日历网格
        if (calendarDays.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "暂无数据",
                    color = AppColors.TextSecondary
                )
            }
        } else {
            // 按7天一行分组
            val rows = calendarDays.chunked(7)
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                rows.forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // 确保每行7个元素
                        val paddedWeek = week + List(7 - week.size) {
                            CalendarDay(0, "", 0, false)
                        }
                        paddedWeek.forEach { day ->
                            CalendarDayCell(day = day, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 图例
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("少", fontSize = 11.sp, color = AppColors.TextHint)
            Spacer(modifier = Modifier.width(4.dp))
            listOf(0, 1, 5, 10, 20).forEach { level ->
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            getCalendarHeatmapColor(level),
                            RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.width(2.dp))
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text("多", fontSize = 11.sp, color = AppColors.TextHint)
        }
    }
}

@Composable
private fun CalendarDayCell(day: CalendarDay, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (day.dayOfMonth > 0) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(6.dp))
                    .background(getCalendarHeatmapColor(day.count)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day.dayOfMonth.toString(),
                    fontSize = 12.sp,
                    fontWeight = if (day.count > 0) FontWeight.Bold else FontWeight.Normal,
                    color = if (day.count >= 10) Color.White else AppColors.TextSecondary
                )
            }
        }
    }
}

private fun getCalendarHeatmapColor(count: Int): Color {
    return when {
        count == 0 -> Color(0xFFF3F4F6)
        count < 5 -> Color(0xFFDCFCE7)
        count < 10 -> Color(0xFF86EFAC)
        count < 20 -> Color(0xFF22C55E)
        else -> Color(0xFF15803D)
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    subValue: String,
    color: Color
) {
    AppCard(modifier = modifier) {
        Text(
            label,
            fontSize = 14.sp,
            color = AppColors.TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                value,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                subValue,
                fontSize = 14.sp,
                color = AppColors.TextSecondary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}
