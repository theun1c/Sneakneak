package com.example.sneakneak.ui.auth.common

// Переиспользуемые composable-компоненты auth-графа.
// Содержат только UI и не знают о domain/data логике.

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.sneakneak.ui.components.BackButton
import com.example.sneakneak.ui.theme.AppColors
import com.example.sneakneak.ui.theme.AppSpacing

@Composable
fun AuthScreenScaffold(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    onBackClick: (() -> Unit)? = null,
    bottomContent: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Surface)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        if (showBackButton) {
            BackButton(onClick = { onBackClick?.invoke() })
        } else {
            Spacer(modifier = Modifier.height(44.dp))
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.displayLarge,
            color = AppColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(AppSpacing.sm))
        Text(
            text = subtitle,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge,
            color = AppColors.TextMuted,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(42.dp))

        Column(content = content)

        Spacer(modifier = Modifier.height(48.dp))
        bottomContent?.invoke()
    }
}

@Composable
fun AuthBottomLink(
    prefix: String,
    action: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = prefix,
            style = MaterialTheme.typography.bodyLarge,
            color = AppColors.TextMuted,
        )
        Text(
            text = action,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = AppColors.TextPrimary,
            modifier = Modifier.clickable(onClick = onClick),
        )
    }
}

@Composable
fun InlineActionText(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = AppColors.TextMuted,
) {
    Text(
        text = text,
        modifier = modifier.clickable(onClick = onClick),
        style = MaterialTheme.typography.bodyMedium,
        color = color,
    )
}

@Composable
fun ConsentRow(
    checked: Boolean,
    text: String,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(
                    color = if (checked) AppColors.Primary else AppColors.SurfaceVariant,
                    shape = CircleShape,
                )
                .border(
                    width = 1.dp,
                    color = if (checked) AppColors.Primary else AppColors.Border,
                    shape = CircleShape,
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { onCheckedChange(!checked) },
            contentAlignment = Alignment.Center,
        ) {
            if (checked) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(AppColors.Surface, CircleShape),
                )
            }
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = AppColors.TextSecondary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable { onCheckedChange(!checked) },
        )
    }
}

@Composable
fun OtpInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    length: Int = 6,
    activeIndex: Int = value.length.coerceAtMost(5),
) {
    BasicTextField(
        value = value,
        onValueChange = { onValueChange(it.take(length).filter(Char::isDigit)) },
        modifier = modifier.fillMaxWidth(),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                repeat(length) { index ->
                    val char = value.getOrNull(index)?.toString().orEmpty()
                    val isActive = index == activeIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(92.dp)
                            .background(AppColors.SurfaceVariant, MaterialTheme.shapes.medium)
                            .border(
                                width = 1.dp,
                                color = when {
                                    isError -> AppColors.Error
                                    isActive -> AppColors.Error
                                    else -> Color.Transparent
                                },
                                shape = MaterialTheme.shapes.medium,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (char.isEmpty()) "0" else char,
                            style = MaterialTheme.typography.titleLarge,
                            color = AppColors.TextPrimary,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        },
    )
}
