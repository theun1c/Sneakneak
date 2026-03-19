package com.example.sneakneak.ui.components

// Поисковая строка для Home/Catalog с опциональной кнопкой фильтра.

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sneakneak.ui.theme.AppColors
import com.example.sneakneak.ui.theme.AppSpacing

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Поиск",
    onFilterClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = placeholder,
            trailingIcon = {
                AppIcon(
                    asset = AppIconAsset.Search,
                    contentDescription = "Поиск",
                    tint = AppColors.TextMuted,
                    modifier = Modifier.size(20.dp),
                )
            },
        )

        if (onFilterClick != null) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(AppColors.Primary, CircleShape)
                    .clickable(onClick = onFilterClick),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(
                    asset = AppIconAsset.Filter,
                    contentDescription = "Фильтр",
                    tint = Color.White,
                )
            }
        }
    }
}
