package com.example.sneakneak.ui.components

// Унифицированная кнопка "назад" для auth и main экранов.

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.sneakneak.ui.theme.AppColors

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCircularIconButton(
        asset = AppIconAsset.ArrowLeft,
        onClick = onClick,
        contentDescription = "Назад",
        modifier = modifier,
        containerColor = AppColors.SurfaceVariant,
    )
}
