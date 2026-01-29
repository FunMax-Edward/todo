package com.edward.todov2.presentation.unitlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edward.todov2.ui.*
import kotlin.random.Random

@Composable
fun HomeScreen(
    viewModel: UnitListViewModel,
    onUnitClick: (Int) -> Unit,
    onAddProjectClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    AppScaffold(
        title = uiState.activeProject?.name ?: "我的题库",
        actions = {
            // 切换题集按钮
            if (uiState.allProjects.isNotEmpty()) {
                AppSecondaryButton(
                    text = "切换",
                    onClick = { viewModel.onAction(UnitListAction.ShowProjectSelector) }
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            // 新建题集按钮
            Button(
                onClick = onAddProjectClick,
                shape = RoundedCornerShape(AppDimens.SmallCornerRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("新建", fontSize = 14.sp)
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColors.Primary)
                }
            }
            uiState.activeProject == null -> {
                EmptyState(
                    icon = "📚",
                    title = "还没有题集",
                    subtitle = "创建你的第一个题集开始刷题吧！",
                    actionText = "新建题集",
                    onAction = onAddProjectClick,
                    modifier = Modifier.padding(padding)
                )
            }
            uiState.units.isEmpty() -> {
                EmptyState(
                    icon = "📋",
                    title = "该题集暂无单元",
                    subtitle = "请检查题集配置",
                    modifier = Modifier.padding(padding)
                )
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = AppDimens.ScreenPadding),
                    horizontalArrangement = Arrangement.spacedBy(AppDimens.ItemSpacing),
                    verticalArrangement = Arrangement.spacedBy(AppDimens.ItemSpacing),
                    contentPadding = PaddingValues(
                        top = AppDimens.ItemSpacing,
                        bottom = 100.dp
                    )
                ) {
                    items(uiState.units) { unit ->
                        UnitGridCard(unit, onClick = { onUnitClick(unit.unit.id) })
                    }
                }
            }
        }
    }

    // 题集选择弹窗
    if (uiState.showProjectSelector) {
        ProjectSelectorDialog(
            projects = uiState.allProjects,
            currentProjectId = uiState.activeProject?.id,
            onSelect = { projectId ->
                viewModel.onAction(UnitListAction.ActivateProject(projectId))
            },
            onDismiss = { viewModel.onAction(UnitListAction.DismissProjectSelector) }
        )
    }
}

@Composable
private fun ProjectSelectorDialog(
    projects: List<ProjectUiModel>,
    currentProjectId: Int?,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择题集", fontWeight = FontWeight.Bold) },
        text = {
            if (projects.isEmpty()) {
                Text("暂无题集", color = AppColors.TextSecondary)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(projects) { project ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelect(project.project.id) },
                            shape = RoundedCornerShape(8.dp),
                            color = if (project.project.id == currentProjectId)
                                AppColors.Primary.copy(alpha = 0.1f) else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        project.project.name,
                                        fontWeight = FontWeight.Medium,
                                        color = AppColors.TextPrimary
                                    )
                                    Text(
                                        "${project.unitCount} 单元 · ${project.totalProblems} 题",
                                        fontSize = 13.sp,
                                        color = AppColors.TextSecondary
                                    )
                                }
                                if (project.project.id == currentProjectId) {
                                    AppChip("当前")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("关闭")
            }
        }
    )
}

@Composable
fun UnitGridCard(unit: UnitItemUiModel, onClick: () -> Unit) {
    // Glassmorphism card look with color preview
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background: Heatmap color grid (actual colors)
            HeatmapPreview(
                levelDistribution = unit.levelDistribution,
                totalProblems = unit.totalProblems,
                modifier = Modifier.fillMaxSize()
            )

            // Light frosted glass overlay - only at bottom for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.6f),
                                Color.White.copy(alpha = 0.85f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )

            // Centered Title at bottom
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = unit.unit.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${unit.masteredCount}/${unit.totalProblems} 已掌握",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun HeatmapPreview(
    levelDistribution: Map<Int, Int>,
    totalProblems: Int,
    modifier: Modifier = Modifier
) {
    // Create a grid of colors based on the actual distribution
    // Use remember with key to avoid reshuffling on every recomposition
    val shuffledColors = remember(levelDistribution, totalProblems) {
        val colors = mutableListOf<Color>()

        // Add colors for each level based on count
        for (level in 0..5) {
            val count = levelDistribution[level] ?: 0
            repeat(count) {
                colors.add(ProficiencyPalette.colorFor(level))
            }
        }

        // If no problems yet, show default grey
        if (colors.isEmpty()) {
            colors.addAll(List(16) { ProficiencyPalette.colorFor(0) })
        }

        // Shuffle with a stable seed based on totalProblems
        colors.shuffled(Random(totalProblems.toLong()))
    }

    // Create a grid of small color boxes
    val columns = 4
    val rows = (shuffledColors.size + columns - 1) / columns

    Column(
        modifier = modifier.padding(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        for (row in 0 until rows.coerceAtMost(5)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    val color = if (index < shuffledColors.size) {
                        shuffledColors[index]
                    } else {
                        Color.Transparent
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                }
            }
        }
    }
}
