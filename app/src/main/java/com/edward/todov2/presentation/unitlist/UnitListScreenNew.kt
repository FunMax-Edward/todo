package com.edward.todov2.presentation.unitlist

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edward.todov2.ui.ProficiencyPalette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitListScreen(
    viewModel: UnitListViewModel,
    onUnitClick: (Int) -> Unit,
    onAddClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        "我的题库",
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("添加单元", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                expanded = true
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                uiState.units.isEmpty() -> {
                    EmptyState(
                        onAddClick = onAddClick
                    )
                }
                else -> {
                    UnitList(
                        units = uiState.units,
                        onUnitClick = { unitId ->
                            viewModel.onAction(UnitListAction.UnitClicked(unitId))
                            onUnitClick(unitId)
                        },
                        onDeleteClick = { unitId ->
                            viewModel.onAction(UnitListAction.DeleteUnit(unitId))
                        }
                    )
                }
            }
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
private fun EmptyState(onAddClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Large icon placeholder
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                "还没有题库",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                "点击下方按钮开始添加\n支持批量输入如: U1:20 U2:18",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onAddClick,
                modifier = Modifier.padding(top = 16.dp),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("添加第一个单元", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun UnitList(
    units: List<UnitItemUiModel>,
    onUnitClick: (Int) -> Unit,
    onDeleteClick: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = units,
            key = { it.unit.id }
        ) { unitModel ->
            UnitCard(
                unitModel = unitModel,
                onClick = { onUnitClick(unitModel.unit.id) },
                onDeleteClick = { onDeleteClick(unitModel.unit.id) },
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Composable
private fun UnitCard(
    unitModel: UnitItemUiModel,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        unitModel.unit.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "${unitModel.totalProblems} 道题",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { showDeleteConfirm = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "删除")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "已掌握",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${unitModel.masteredCount} / ${unitModel.totalProblems}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = ProficiencyPalette.colorFor(5)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Animated progress bar
            val animatedProgress by animateFloatAsState(
                targetValue = unitModel.progressPercentage,
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                label = "progress"
            )

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = ProficiencyPalette.colorFor(5),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Level distribution chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (level in 1..4) {
                    val count = unitModel.levelDistribution[level] ?: 0
                    if (count > 0) {
                        LevelChip(level = level, count = count)
                    }
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = { Text("确认删除") },
            text = { Text("确定要删除「${unitModel.unit.name}」吗？这将删除所有相关题目和记录。") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteClick()
                        showDeleteConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("取消")
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun LevelChip(level: Int, count: Int) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = ProficiencyPalette.colorFor(level).copy(alpha = 0.15f),
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(ProficiencyPalette.colorFor(level), CircleShape)
            )
            Text(
                count.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ProficiencyPalette.colorFor(level)
            )
        }
    }
}

@Composable
private fun AddUnitDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    modifier = Modifier.padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        },
        title = {
            Text(
                "添加题库单元",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "支持批量输入，格式如：",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "U1: 20 U2: 18 U3: 25",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                        Text(
                            "或每行一个：",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Unit1: 10\nUnit2: 20",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    label = { Text("输入单元和题数") },
                    placeholder = { Text("U1: 20 U2: 18 ...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(inputText) },
                enabled = inputText.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("开始生成", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        shape = RoundedCornerShape(28.dp)
    )
}
