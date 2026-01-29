package com.edward.todov2.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 统一的应用设计系统
 * 所有页面使用相同的颜色、间距、组件样式
 */
object AppColors {
    // 主背景色
    val Background = Color(0xFFF5F5F5)

    // 卡片背景
    val CardBackground = Color.White

    // 主色调
    val Primary = Color(0xFF2196F3)
    val PrimaryVariant = Color(0xFF1976D2)

    // 成功色（绿色）
    val Success = Color(0xFF4CAF50)
    val SuccessLight = Color(0xFFE8F5E9)

    // 错误色（红色）
    val Error = Color(0xFFE53935)
    val ErrorLight = Color(0xFFFFEBEE)

    // 文字颜色
    val TextPrimary = Color(0xFF212121)
    val TextSecondary = Color(0xFF757575)
    val TextHint = Color(0xFFBDBDBD)

    // 分割线
    val Divider = Color(0xFFE0E0E0)
}

object AppDimens {
    val ScreenPadding = 16.dp
    val CardPadding = 20.dp
    val ItemSpacing = 16.dp
    val SmallSpacing = 8.dp

    val CardCornerRadius = 16.dp
    val ButtonCornerRadius = 12.dp
    val SmallCornerRadius = 8.dp

    val CardElevation = 2.dp
}

/**
 * 统一的页面容器
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            Surface(
                color = AppColors.Background,
                shadowElevation = 0.dp
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppDimens.ScreenPadding),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextPrimary
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            content = actions
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        },
        floatingActionButton = floatingActionButton,
        containerColor = AppColors.Background,
        content = content
    )
}

/**
 * 统一的卡片样式
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(AppDimens.CardCornerRadius),
        colors = CardDefaults.cardColors(containerColor = AppColors.CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = AppDimens.CardElevation),
        onClick = onClick ?: {}
    ) {
        Column(
            modifier = Modifier.padding(AppDimens.CardPadding),
            content = content
        )
    }
}

/**
 * 主要按钮
 */
@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(AppDimens.ButtonCornerRadius),
        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Success),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * 次要按钮（白色背景）
 */
@Composable
fun AppSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(AppDimens.SmallCornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.CardBackground,
            contentColor = AppColors.TextPrimary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

/**
 * 统一的输入框样式
 */
@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(placeholder, color = AppColors.TextHint) },
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(AppDimens.SmallCornerRadius),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFFAFAFA),
            unfocusedContainerColor = Color(0xFFFAFAFA),
            focusedBorderColor = AppColors.Primary,
            unfocusedBorderColor = AppColors.Divider
        )
    )
}

/**
 * 区块标题
 */
@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = AppColors.TextPrimary,
        modifier = modifier
    )
}

/**
 * 小标签
 */
@Composable
fun AppChip(
    text: String,
    color: Color = AppColors.Success,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = color.copy(alpha = 0.15f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

/**
 * 空状态组件
 */
@Composable
fun EmptyState(
    icon: String,
    title: String,
    subtitle: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(icon, fontSize = 64.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            title,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            subtitle,
            fontSize = 16.sp,
            color = AppColors.TextSecondary,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            AppPrimaryButton(
                text = actionText,
                onClick = onAction,
                modifier = Modifier.width(200.dp)
            )
        }
    }
}
