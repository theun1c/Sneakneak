package com.example.sneakneak.ui.components

// Базовые диалоги приложения (успех/информация/ошибка).
// Используются экранами для обязательного показа ошибок через modal window.

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sneakneak.ui.theme.AppColors

@Composable
fun SuccessDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    confirmText: String = "OK",
) {
    BaseAppDialog(title, message, onDismiss, confirmText, showSuccessIcon = true)
}

@Composable
fun InfoDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    confirmText: String = "OK",
) {
    BaseAppDialog(title, message, onDismiss, confirmText)
}

@Composable
private fun BaseAppDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    confirmText: String,
    showSuccessIcon: Boolean = false,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppColors.Surface,
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (showSuccessIcon) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .padding(bottom = 4.dp),
                    ) {
                        androidx.compose.material3.Surface(
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = AppColors.Primary,
                        ) {
                            Box {
                                AppIcon(
                                    asset = AppIconAsset.Mail,
                                    contentDescription = "Success",
                                    modifier = Modifier
                                        .size(20.dp)
                                        .align(androidx.compose.ui.Alignment.Center),
                                )
                            }
                        }
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = AppColors.TextPrimary,
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextSecondary,
                )
            }
        },
        text = {},
        confirmButton = {
            PrimaryButton(
                text = confirmText,
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
            )
        },
    )
}
