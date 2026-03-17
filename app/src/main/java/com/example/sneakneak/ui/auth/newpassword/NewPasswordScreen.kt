package com.example.sneakneak.ui.auth.newpassword

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
import com.example.sneakneak.ui.components.InfoDialog
import com.example.sneakneak.ui.components.PasswordTextField
import com.example.sneakneak.ui.components.PrimaryButton
import com.example.sneakneak.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

// New password screen completes the mock recovery chain but already exposes the same
// state shape needed for the future real backend flow.
data class NewPasswordUiState(
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val dialogMessage: String? = null,
)

sealed interface NewPasswordUiEvent {
    data class PasswordChanged(val value: String) : NewPasswordUiEvent
    data class ConfirmPasswordChanged(val value: String) : NewPasswordUiEvent
    data object PasswordVisibilityToggled : NewPasswordUiEvent
    data object ConfirmPasswordVisibilityToggled : NewPasswordUiEvent
    data object SaveClicked : NewPasswordUiEvent
    data object BackClicked : NewPasswordUiEvent
    data object DialogDismissed : NewPasswordUiEvent
}

sealed interface NewPasswordUiEffect {
    data object NavigateBack : NewPasswordUiEffect
    data object NavigateToSignIn : NewPasswordUiEffect
}

class NewPasswordViewModel(
    private val email: String,
    private val useCases: AuthUseCases,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    var uiState by mutableStateOf(NewPasswordUiState())
        private set

    var uiEffect by mutableStateOf<NewPasswordUiEffect?>(null)
        private set

    fun onEvent(event: NewPasswordUiEvent) {
        when (event) {
            is NewPasswordUiEvent.PasswordChanged -> uiState = uiState.copy(password = event.value)
            is NewPasswordUiEvent.ConfirmPasswordChanged -> uiState = uiState.copy(confirmPassword = event.value)
            NewPasswordUiEvent.PasswordVisibilityToggled -> {
                uiState = uiState.copy(isPasswordVisible = !uiState.isPasswordVisible)
            }
            NewPasswordUiEvent.ConfirmPasswordVisibilityToggled -> {
                uiState = uiState.copy(isConfirmPasswordVisible = !uiState.isConfirmPasswordVisible)
            }
            NewPasswordUiEvent.BackClicked -> uiEffect = NewPasswordUiEffect.NavigateBack
            NewPasswordUiEvent.DialogDismissed -> uiState = uiState.copy(dialogMessage = null)
            NewPasswordUiEvent.SaveClicked -> scope.launch {
                uiState = uiState.copy(isLoading = true, dialogMessage = null)
                // TODO(DATA): bind to repository-backed password update after recovery session is real.
                when (val result = useCases.updatePassword(email, uiState.password, uiState.confirmPassword)) {
                    is AuthResult.Error -> {
                        uiState = uiState.copy(isLoading = false, dialogMessage = result.message)
                    }

                    is AuthResult.Success -> {
                        uiState = uiState.copy(isLoading = false)
                        uiEffect = NewPasswordUiEffect.NavigateToSignIn
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
fun NewPasswordRoute(
    email: String,
    viewModel: NewPasswordViewModel = remember(email) { NewPasswordViewModel(email, AppContainer.authUseCases) },
    onBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
) {
    val effect = viewModel.uiEffect
    LaunchedEffect(effect) {
        when (effect) {
            NewPasswordUiEffect.NavigateBack -> {
                onBack()
                viewModel.consumeEffect()
            }

            NewPasswordUiEffect.NavigateToSignIn -> {
                onNavigateToSignIn()
                viewModel.consumeEffect()
            }

            null -> Unit
        }
    }

    NewPasswordScreen(
        state = viewModel.uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
fun NewPasswordScreen(
    state: NewPasswordUiState,
    onEvent: (NewPasswordUiEvent) -> Unit,
) {
    AuthScreenScaffold(
        title = "Задать Новый Пароль",
        subtitle = "Установите Новый Пароль Для Входа В\nВашу Учетную Запись",
        onBackClick = { onEvent(NewPasswordUiEvent.BackClicked) },
    ) {
        PasswordTextField(
            value = state.password,
            onValueChange = { onEvent(NewPasswordUiEvent.PasswordChanged(it)) },
            label = "Пароль",
            placeholder = "********",
            isPasswordVisible = state.isPasswordVisible,
            onVisibilityToggle = { onEvent(NewPasswordUiEvent.PasswordVisibilityToggled) },
        )
        Spacer(modifier = Modifier.height(24.dp))
        PasswordTextField(
            value = state.confirmPassword,
            onValueChange = { onEvent(NewPasswordUiEvent.ConfirmPasswordChanged(it)) },
            label = "Подтверждение пароля",
            placeholder = "********",
            isPasswordVisible = state.isConfirmPasswordVisible,
            onVisibilityToggle = { onEvent(NewPasswordUiEvent.ConfirmPasswordVisibilityToggled) },
        )
        Spacer(modifier = Modifier.height(32.dp))
        PrimaryButton(
            text = "Сохранить",
            onClick = { onEvent(NewPasswordUiEvent.SaveClicked) },
            loading = state.isLoading,
        )
    }

    state.dialogMessage?.let {
        InfoDialog(
            title = "Ошибка",
            message = it,
            onDismiss = { onEvent(NewPasswordUiEvent.DialogDismissed) },
        )
    }
}

@Preview
@Composable
private fun NewPasswordPreview() {
    AppTheme {
        NewPasswordScreen(
            state = NewPasswordUiState(password = "12345678", confirmPassword = "12345678"),
            onEvent = {},
        )
    }
}
