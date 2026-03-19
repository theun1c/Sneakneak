package com.example.sneakneak.ui.screens

// Legacy-обертка над SignUpScreen для старых точек входа/превью.

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.sneakneak.ui.auth.signup.SignUpScreen
import com.example.sneakneak.ui.auth.signup.SignUpUiState
import com.example.sneakneak.ui.theme.AppTheme

@Composable
fun RegisterScreen() {
    SignUpScreen(
        state = SignUpUiState(),
        onEvent = {},
    )
}

@Preview
@Composable
private fun RegisterScreenPreview() {
    AppTheme {
        RegisterScreen()
    }
}
