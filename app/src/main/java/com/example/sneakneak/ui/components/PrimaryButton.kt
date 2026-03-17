package com.example.sneakneak.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sneakneak.ui.theme.AppColors

// Shared CTA button for auth and editable screens.
// Disabled colors intentionally preserve the Figma contrast used in the registration mockups.
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(vertical = 18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.Primary,
            contentColor = AppColors.OnPrimary,
            disabledContainerColor = AppColors.PrimaryDark,
            disabledContentColor = AppColors.OnPrimary,
        ),
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = AppColors.OnPrimary,
                strokeWidth = 2.dp,
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}
