package com.example.sneakneak.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.sneakneak.ui.theme.AppColors

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String = "",
    enabled: Boolean = true,
    isPasswordVisible: Boolean? = null,
    onVisibilityToggle: (() -> Unit)? = null,
) {
    var internalVisibility by remember { mutableStateOf(false) }
    val isVisible = isPasswordVisible ?: internalVisibility
    val toggleVisibility = onVisibilityToggle ?: { internalVisibility = !internalVisibility }

    AppTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            AppIcon(
                asset = if (isVisible) AppIconAsset.Eye else AppIconAsset.EyeOff,
                contentDescription = if (isVisible) "Скрыть пароль" else "Показать пароль",
                tint = AppColors.TextMuted,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = toggleVisibility),
            )
        },
    )
}
