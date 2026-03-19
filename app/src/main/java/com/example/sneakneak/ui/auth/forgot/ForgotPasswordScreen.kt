package com.example.sneakneak.ui.auth.forgot

// Экран запроса восстановления пароля.
// При успехе открывает OTP-экран через UiEffect, не выполняя навигацию напрямую из composable.

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.sneakneak.di.AppContainer
import com.example.sneakneak.domain.auth.model.AuthResult
import com.example.sneakneak.domain.auth.usecase.AuthUseCases
import com.example.sneakneak.ui.auth.common.AuthScreenScaffold
import com.example.sneakneak.ui.components.AppTextField
import com.example.sneakneak.ui.components.InfoDialog
import com.example.sneakneak.ui.components.PrimaryButton
import com.example.sneakneak.ui.components.SuccessDialog
import com.example.sneakneak.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

// Recovery request screen state. Success dialog is tracked in state because it is part of
// the visual UX contract, while OTP navigation is emitted as one-off effect.
data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val dialogMessage: String? = null,
    val showSuccessDialog: Boolean = false,
)

sealed interface ForgotPasswordUiEvent {
    data class EmailChanged(val value: String) : ForgotPasswordUiEvent
    data object SendClicked : ForgotPasswordUiEvent
    data object BackClicked : ForgotPasswordUiEvent
    data object SuccessDialogConfirmed : ForgotPasswordUiEvent
    data object ErrorDismissed : ForgotPasswordUiEvent
}

sealed interface ForgotPasswordUiEffect {
    data object NavigateBack : ForgotPasswordUiEffect
    data class NavigateToOtp(val email: String) : ForgotPasswordUiEffect
}

class ForgotPasswordViewModel(
    private val useCases: AuthUseCases,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    var uiState by mutableStateOf(ForgotPasswordUiState())
        private set

    var uiEffect by mutableStateOf<ForgotPasswordUiEffect?>(null)
        private set

    fun onEvent(event: ForgotPasswordUiEvent) {
        when (event) {
            is ForgotPasswordUiEvent.EmailChanged -> uiState = uiState.copy(email = event.value)
            ForgotPasswordUiEvent.BackClicked -> uiEffect = ForgotPasswordUiEffect.NavigateBack
            ForgotPasswordUiEvent.ErrorDismissed -> uiState = uiState.copy(dialogMessage = null)
            ForgotPasswordUiEvent.SendClicked -> scope.launch {
                uiState = uiState.copy(isLoading = true, dialogMessage = null)
                // Uses real Supabase auth when configured; otherwise falls back to fake repository.
                when (val result = useCases.sendRecoveryCode(uiState.email)) {
                    is AuthResult.Error -> {
                        uiState = uiState.copy(isLoading = false, dialogMessage = result.message)
                    }

                    is AuthResult.Success -> {
                        uiState = uiState.copy(isLoading = false, showSuccessDialog = true)
                    }
                }
            }

            ForgotPasswordUiEvent.SuccessDialogConfirmed -> {
                uiState = uiState.copy(showSuccessDialog = false)
                uiEffect = ForgotPasswordUiEffect.NavigateToOtp(uiState.email)
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
fun ForgotPasswordRoute(
    viewModel: ForgotPasswordViewModel = remember { ForgotPasswordViewModel(AppContainer.authUseCases) },
    onBack: () -> Unit,
    onNavigateToOtp: (String) -> Unit,
) {
    val effect = viewModel.uiEffect
    LaunchedEffect(effect) {
        when (effect) {
            ForgotPasswordUiEffect.NavigateBack -> {
                onBack()
                viewModel.consumeEffect()
            }

            is ForgotPasswordUiEffect.NavigateToOtp -> {
                onNavigateToOtp(effect.email)
                viewModel.consumeEffect()
            }

            null -> Unit
        }
    }

    ForgotPasswordScreen(
        state = viewModel.uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordUiState,
    onEvent: (ForgotPasswordUiEvent) -> Unit,
) {
    AuthScreenScaffold(
        title = "Забыл Пароль",
        subtitle = "Введите Свою Учетную Запись\nДля Сброса",
        onBackClick = { onEvent(ForgotPasswordUiEvent.BackClicked) },
    ) {
        AppTextField(
            value = state.email,
            onValueChange = { onEvent(ForgotPasswordUiEvent.EmailChanged(it)) },
            placeholder = "xyz@gmail.com",
        )
        Spacer(modifier = Modifier.height(28.dp))
        PrimaryButton(
            text = "Отправить",
            onClick = { onEvent(ForgotPasswordUiEvent.SendClicked) },
            loading = state.isLoading,
        )
    }

    state.dialogMessage?.let {
        InfoDialog(
            title = "Ошибка",
            message = it,
            onDismiss = { onEvent(ForgotPasswordUiEvent.ErrorDismissed) },
        )
    }

    if (state.showSuccessDialog) {
        SuccessDialog(
            title = "Проверьте Ваш Email",
            message = "Мы Отправили Код Восстановления Пароля На Вашу Электронную Почту.",
            onDismiss = { onEvent(ForgotPasswordUiEvent.SuccessDialogConfirmed) },
        )
    }
}

@Preview
@Composable
private fun ForgotPasswordPreview() {
    AppTheme {
        ForgotPasswordScreen(
            state = ForgotPasswordUiState(),
            onEvent = {},
        )
    }
}

@Preview
@Composable
private fun ForgotPasswordDialogPreview() {
    AppTheme {
        ForgotPasswordScreen(
            state = ForgotPasswordUiState(showSuccessDialog = true, email = "user@mail.com"),
            onEvent = {},
        )
    }
}
