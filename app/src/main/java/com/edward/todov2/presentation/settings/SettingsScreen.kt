package com.edward.todov2.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edward.todov2.presentation.problemgrid.ProblemLabelFormat
import com.edward.todov2.ui.*

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    var showClearDialog by remember { mutableStateOf(false) }
    val currentFormat by viewModel.labelFormat.collectAsState()

    AppScaffold(title = "设置") { padding ->
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
            // 显示设置
            item {
                AppCard {
                    Text(
                        "显示设置",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "自定义题号的显示格式",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "题号格式",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AppColors.Primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FormatSelection(currentFormat, onSelect = { viewModel.updateLabelFormat(it) })
                }
            }

            // 危险区域
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(AppDimens.CardCornerRadius),
                    colors = CardDefaults.cardColors(containerColor = AppColors.CardBackground),
                    border = BorderStroke(1.dp, AppColors.Error.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(AppDimens.CardPadding)) {
                        Text(
                            "危险操作",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.Error
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "以下操作不可撤销，请谨慎使用",
                            fontSize = 14.sp,
                            color = AppColors.TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showClearDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Error),
                            shape = RoundedCornerShape(AppDimens.SmallCornerRadius)
                        ) {
                            Text("清除所有数据", fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // 关于
            item {
                AppCard {
                    Text(
                        "关于",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "刷题助手 v1.0",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "帮助你系统地跟踪学习进度",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("确认清除数据", fontWeight = FontWeight.Bold) },
            text = { Text("确定要删除所有题集、单元和做题记录吗？此操作无法撤销。") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Error)
                ) {
                    Text("删除全部")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("取消") }
            }
        )
    }
}

@Composable
private fun FormatSelection(current: ProblemLabelFormat, onSelect: (ProblemLabelFormat) -> Unit) {
    Column {
        ProblemLabelFormat.entries.forEach { format ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = (format == current),
                    onClick = { onSelect(format) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = AppColors.Primary
                    )
                )
                Text(
                    text = format.displayName,
                    fontSize = 15.sp,
                    color = AppColors.TextPrimary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
