package com.edward.todov2.presentation.problemgrid

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edward.todov2.ui.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemGridScreen(
    viewModel: ProblemGridViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            Surface(color = AppColors.Background) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 8.dp, start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回",
                                tint = AppColors.TextPrimary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("返回", color = AppColors.TextPrimary, fontWeight = FontWeight.Medium)
                        }
                    }
                    
                    Text(
                        text = uiState.unitName,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary,
                        modifier = Modifier.padding(start = 24.dp, bottom = 16.dp)
                    )
                }
            }
        },
        containerColor = AppColors.Background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    LoadingState()
                }
                uiState.problems.isEmpty() -> {
                    EmptyState()
                }
                else -> {
                    ProblemGrid(
                        problems = uiState.problems,
                        onProblemClick = { problem ->
                            viewModel.onAction(ProblemGridAction.ProblemClicked(problem))
                        },
                        onProblemLongPress = { problem ->
                            viewModel.onAction(ProblemGridAction.ProblemLongPressed(problem))
                        }
                    )
                }
            }
        }

        // Dialogs
        uiState.selectedProblem?.let { problem ->
            MarkResultDialog(
                problem = problem,
                onDismiss = { viewModel.onAction(ProblemGridAction.DismissDialog) },
                onResult = { isCorrect ->
                    viewModel.onAction(ProblemGridAction.MarkResult(problem, isCorrect))
                }
            )
        }

        uiState.detailProblem?.let { problem ->
            ProblemDetailDialog(
                problem = problem,
                onDismiss = { viewModel.onAction(ProblemGridAction.DismissDialog) }
            )
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                "暂无题目",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "请在设置中添加题目",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProblemGrid(
    problems: List<ProblemUiModel>,
    onProblemClick: (ProblemUiModel) -> Unit,
    onProblemLongPress: (ProblemUiModel) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 80.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = problems,
            key = { it.problem.id }
        ) { problemUi ->
            ProblemTile(
                problem = problemUi,
                onClick = { onProblemClick(problemUi) },
                onLongPress = { onProblemLongPress(problemUi) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ProblemTile(
    problem: ProblemUiModel,
    onClick: () -> Unit,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    // Animated background color
    val animatedColor by animateColorAsState(
        targetValue = problem.backgroundColor,
        animationSpec = tween(durationMillis = 300),
        label = "backgroundColor"
    )

    Surface(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp)),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedColor)
                .combinedClickable(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onClick()
                    },
                    onLongClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onLongPress()
                    },
                    onClickLabel = "标记完成",
                    onLongClickLabel = "查看详情"
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = problem.label,
                color = problem.textColor,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MarkResultDialog(
    problem: ProblemUiModel,
    onDismiss: () -> Unit,
    onResult: (Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = problem.backgroundColor.copy(alpha = 0.2f)
            ) {
                Box(
                    modifier = Modifier.padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        problem.label,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = problem.backgroundColor
                    )
                }
            }
        },
        title = {
            Text(
                "记录本次结果",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "当前状态: ${problem.levelLabel}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { onResult(false) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF4444)
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                "做错了",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Button(
                        onClick = { onResult(true) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981)
                        ),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                "做对了",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("取消")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun ProblemDetailDialog(
    problem: ProblemUiModel,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = problem.backgroundColor
            ) {
                Box(
                    modifier = Modifier.padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        problem.label,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = problem.textColor
                    )
                }
            }
        },
        title = {
            Text(
                "题目详情",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatRow(
                    label = "当前状态",
                    value = problem.levelLabel,
                    valueColor = problem.backgroundColor
                )

                HorizontalDivider()

                StatRow(
                    label = "✅ 正确次数",
                    value = "${problem.problem.correctCount}",
                    valueColor = Color(0xFF10B981)
                )

                StatRow(
                    label = "❌ 错误次数",
                    value = "${problem.problem.wrongCount}",
                    valueColor = Color(0xFFEF4444)
                )

                val total = problem.problem.correctCount + problem.problem.wrongCount
                val accuracy = if (total > 0) {
                    (problem.problem.correctCount.toFloat() / total * 100).toInt()
                } else 0

                StatRow(
                    label = "正确率",
                    value = if (total > 0) "$accuracy%" else "暂无数据",
                    valueColor = MaterialTheme.colorScheme.primary
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("完成", fontWeight = FontWeight.Bold)
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}
