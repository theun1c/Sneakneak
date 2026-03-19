package com.example.sneakneak.ui.auth.otp

// Экран проверки recovery OTP.
// Поддерживает таймер повторной отправки и error-state для всех ячеек кода.

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.sneakneak.di.AppContainer
import com.example.sneakneak.domain.auth.model.AuthResult
import com.example.sneakneak.domain.auth.usecase.AuthUseCases
import com.example.sneakneak.ui.auth.common.AuthScreenScaffold
import com.example.sneakneak.ui.auth.common.InlineActionText
import com.example.sneakneak.ui.auth.common.OtpInputRow
import com.example.sneakneak.ui.components.InfoDialog
import com.example.sneakneak.ui.theme.AppColors
import com.example.sneakneak.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// OTP screen already models the important UX states from the specification:
// loading, timer countdown, resend availability and whole-row error state.
data class OtpUiState(
    val email: String = "",
    val code: String = "",
    val isCodeError: Boolean = false,
    val isLoading: Boolean = false,
    val secondsRemaining: Int = 60,
    val canResend: Boolean = false,
    val dialogMessage: String? = null,
)

sealed interface OtpUiEvent {
    data class CodeChanged(val value: String) : OtpUiEvent
    data object BackClicked : OtpUiEvent
    data object ContinueClicked : OtpUiEvent
    data object ResendClicked : OtpUiEvent
    data object TimerTicked : OtpUiEvent
    data object DialogDismissed : OtpUiEvent
}

sealed interface OtpUiEffect {
    data object NavigateBack : OtpUiEffect
    data class NavigateToNewPassword(val email: String) : OtpUiEffect
}

class OtpViewModel(
    initialEmail: String = "",
    private val useCases: AuthUseCases,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    var uiState by mutableStateOf(OtpUiState(email = initialEmail))
        private set

    var uiEffect by mutableStateOf<OtpUiEffect?>(null)
        private set

    fun onEvent(event: OtpUiEvent) {
        when (event) {
            is OtpUiEvent.CodeChanged -> uiState = uiState.copy(code = event.value, isCodeError = false)
            OtpUiEvent.BackClicked -> uiEffect = OtpUiEffect.NavigateBack
            OtpUiEvent.DialogDismissed -> uiState = uiState.copy(dialogMessage = null)
            OtpUiEvent.TimerTicked -> {
                if (uiState.canResend) return
                val nextSeconds = (uiState.secondsRemaining - 1).coerceAtLeast(0)
                uiState = uiState.copy(
                    secondsRemaining = nextSeconds,
                    canResend = nextSeconds == 0,
                )
            }

            OtpUiEvent.ResendClicked -> scope.launch {
                if (!uiState.canResend) return@launch
                uiState = uiState.copy(isLoading = true, dialogMessage = null)
                // Reuses the same recovery endpoint as Forgot Password.
                when (val result = useCases.sendRecoveryCode(uiState.email)) {
                    is AuthResult.Error -> {
                        uiState = uiState.copy(isLoading = false, dialogMessage = result.message)
                    }

                    is AuthResult.Success -> {
                        uiState = uiState.copy(
                            isLoading = false,
                            canResend = false,
                            secondsRemaining = 60,
                            isCodeError = false,
                            code = "",
                        )
                    }
                }
            }

            OtpUiEvent.ContinueClicked -> scope.launch {
                uiState = uiState.copy(isLoading = true, dialogMessage = null, isCodeError = false)
                // Uses Supabase OTP verification when configured; fake implementation remains as fallback.
                when (val result = useCases.verifyRecoveryCode(uiState.email, uiState.code)) {
                    is AuthResult.Error -> {
                        uiState = uiState.copy(
                            isLoading = false,
                            dialogMessage = result.message,
                            isCodeError = true,
                        )
                    }

                    is AuthResult.Success -> {
                        uiState = uiState.copy(isLoading = false)
                        uiEffect = OtpUiEffect.NavigateToNewPassword(uiState.email)
                    }
                }
            }
        }
    }

    fun consumeEffect() {
        uiEffect = null
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}

@Composable
fun OtpRoute(
    email: String,
    viewModel: OtpViewModel = remember(email) { OtpViewModel(email, AppContainer.authUseCases) },
    onBack: () -> Unit,
    onNavigateToNewPassword: (String) -> Unit,
) {
    val state = viewModel.uiState

    LaunchedEffect(state.canResend, state.secondsRemaining) {
        if (!state.canResend && state.secondsRemaining > 0) {
            delay(1_000)
            viewModel.onEvent(OtpUiEvent.TimerTicked)
        }
    }

    val effect = viewModel.uiEffect
    LaunchedEffect(effect) {
        when (effect) {
            OtpUiEffect.NavigateBack -> {
                onBack()
                viewModel.consumeEffect()
            }

            is OtpUiEffect.NavigateToNewPassword -> {
                onNavigateToNewPassword(effect.email)
                viewModel.consumeEffect()
            }

            null -> Unit
        }
    }

    OtpScreen(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@Composable
fun OtpScreen(
    state: OtpUiState,
    onEvent: (OtpUiEvent) -> Unit,
) {
    AuthScreenScaffold(
        title = "OTP Проверка",
        subtitle = "Пожалуйста, Проверьте Свою\nЭлектронную Почту, Чтобы Увидеть Код\nПодтверждения",
        onBackClick = { onEvent(OtpUiEvent.BackClicked) },
    ) {
        androidx.compose.material3.Text(
            text = "OTP Код",
            style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
            color = AppColors.TextPrimary,
        )
        Spacer(modifier = Modifier.height(16.dp))
        OtpInputRow(
            value = state.code,
            onValueChange = { onEvent(OtpUiEvent.CodeChanged(it)) },
            isError = state.isCodeError,
            // Для текущей Supabase recovery-конфигурации в проекте используется 8-значный токен.
            length = 8,
            activeIndex = state.code.length.coerceAtMost(7),
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (state.canResend) {
            InlineActionText(
                text = "Отправить заново",
                onClick = { onEvent(OtpUiEvent.ResendClicked) },
            )
        } else {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                androidx.compose.material3.Text(
                    text = "00:${state.secondsRemaining.toString().padStart(2, '0')}",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextMuted,
                    modifier = Modifier.padding(end = 4.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        com.example.sneakneak.ui.components.PrimaryButton(
            text = "Подтвердить",
            onClick = { onEvent(OtpUiEvent.ContinueClicked) },
            loading = state.isLoading,
        )
    }

    state.dialogMessage?.let {
        InfoDialog(
            title = "Ошибка",
            message = it,
            onDismiss = { onEvent(OtpUiEvent.DialogDismissed) },
        )
    }
}

@Preview
@Composable
private fun OtpDefaultPreview() {
    AppTheme {
        OtpScreen(
            state = OtpUiState(code = "00000", secondsRemaining = 30, canResend = false),
            onEvent = {},
        )
    }
}

@Preview
@Composable
private fun OtpAlternatePreview() {
    AppTheme {
        OtpScreen(
            state = OtpUiState(code = "00000", canResend = true, isCodeError = false),
            onEvent = {},
        )
    }
}

@Preview
@Composable
private fun OtpErrorPreview() {
    AppTheme {
        OtpScreen(
            state = OtpUiState(code = "123456", canResend = true, isCodeError = true, dialogMessage = "Неверный код"),
            onEvent = {},
        )
    }
}
