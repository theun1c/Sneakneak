package com.example.sneakneak.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sneakneak.ui.theme.AppColors

@Composable
fun CategoryChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = if (selected) AppColors.Primary else AppColors.Surface,
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) AppColors.OnPrimary else AppColors.TextSecondary,
            )
        }
    }
}
