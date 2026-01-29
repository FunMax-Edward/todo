package com.edward.todov2.presentation.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.edward.todov2.ui.*

@Composable
fun SetupScreen(viewModel: SetupViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    // 创建成功后重置
    LaunchedEffect(uiState.createSuccess) {
        if (uiState.createSuccess) {
            kotlinx.coroutines.delay(1500)
            viewModel.onAction(SetupAction.ResetForm)
        }
    }

    AppScaffold(
        title = "创建题集",
        actions = {
            // 可以添加顶部操作按钮
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = AppDimens.ScreenPadding),
            verticalArrangement = Arrangement.spacedBy(AppDimens.ItemSpacing)
        ) {
            // 创建新题集卡片
            item {
                Spacer(modifier = Modifier.height(8.dp))
                CreateProjectCard(
                    projectName = uiState.projectName,
                    unitDefinitions = uiState.unitDefinitions,
                    isCreating = uiState.isCreating,
                    createSuccess = uiState.createSuccess,
                    errorMessage = uiState.errorMessage,
                    onProjectNameChange = { viewModel.onAction(SetupAction.UpdateProjectName(it)) },
                    onUnitDefinitionsChange = { viewModel.onAction(SetupAction.UpdateUnitDefinitions(it)) },
                    onCreateClick = { viewModel.onAction(SetupAction.CreateProject) }
                )
            }

            // 已有题集
            if (uiState.existingProjects.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    SectionTitle("已有题集")
                }

                items(uiState.existingProjects) { project ->
                    ProjectManageCard(
                        project = project,
                        onDelete = { viewModel.onAction(SetupAction.DeleteProject(project.project.id)) }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
private fun CreateProjectCard(
    projectName: String,
    unitDefinitions: String,
    isCreating: Boolean,
    createSuccess: Boolean,
    errorMessage: String?,
    onProjectNameChange: (String) -> Unit,
    onUnitDefinitionsChange: (String) -> Unit,
    onCreateClick: () -> Unit
) {
    AppCard {
        // 标题
        Text(
            "新建题集",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
        Text(
            "一次性定义整套题的结构",
            fontSize = 14.sp,
            color = AppColors.TextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Step 1: 题集名称
        Text(
            "题集名称",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.Primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        AppTextField(
            value = projectName,
            onValueChange = onProjectNameChange,
            placeholder = "例如：高数1000题"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Step 2: 单元结构
        Text(
            "单元结构",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.Primary
        )
        Text(
            "每行一个单元，格式：单元名: 题数",
            fontSize = 12.sp,
            color = AppColors.TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        AppTextField(
            value = unitDefinitions,
            onValueChange = onUnitDefinitionsChange,
            placeholder = "U1: 32\nU2: 18\nU3: 25",
            singleLine = false,
            minLines = 5
        )

        // 错误提示
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                errorMessage,
                fontSize = 14.sp,
                color = AppColors.Error
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 创建按钮
        Button(
            onClick = onCreateClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isCreating && !createSuccess,
            shape = RoundedCornerShape(AppDimens.ButtonCornerRadius),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (createSuccess) AppColors.Success else AppColors.Primary
            ),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            when {
                isCreating -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("创建中...", fontWeight = FontWeight.Bold)
                }
                createSuccess -> {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("创建成功！", fontWeight = FontWeight.Bold)
                }
                else -> {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("创建题集", fontWeight = FontWeight.Bold)
                }
            }
        }

        // 提示
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "💡 创建后会自动激活，可立即开始刷题",
            fontSize = 12.sp,
            color = AppColors.TextSecondary
        )
    }
}

@Composable
private fun ProjectManageCard(
    project: ProjectManageModel,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    AppCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        project.project.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    if (project.project.isActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        AppChip("当前")
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${project.unitCount} 个单元 · ${project.totalProblems} 道题 · ${project.masteredCount} 已掌握",
                    fontSize = 14.sp,
                    color = AppColors.TextSecondary
                )
            }

            IconButton(
                onClick = { showDeleteDialog = true },
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = AppColors.ErrorLight,
                    contentColor = AppColors.Error
                )
            ) {
                Icon(Icons.Default.Delete, contentDescription = "删除")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除", fontWeight = FontWeight.Bold) },
            text = { Text("删除「${project.project.name}」将清除所有单元和做题记录，无法恢复。") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Error)
                ) {
                    Text("删除")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
