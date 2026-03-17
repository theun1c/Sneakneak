package com.example.sneakneak.ui.auth.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.sneakneak.ui.auth.common.AuthBottomLink
import com.example.sneakneak.ui.auth.common.AuthScreenScaffold
import com.example.sneakneak.ui.auth.common.InlineActionText
import com.example.sneakneak.ui.components.AppTextField
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

// State contract for the current Sign In screen.
// Dialog text and loading are stored here so the composable stays display-only.
data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val dialogMessage: String? = null,
)

sealed interface SignInUiEvent {
    data class EmailChanged(val value: String) : SignInUiEvent
    data class PasswordChanged(val value: String) : SignInUiEvent
    data object SignInClicked : SignInUiEvent
    data object ForgotPasswordClicked : SignInUiEvent
    data object CreateAccountClicked : SignInUiEvent
    data object PasswordVisibilityToggled : SignInUiEvent
    data object DialogDismissed : SignInUiEvent
}

sealed interface SignInUiEffect {
    data object NavigateToForgotPassword : SignInUiEffect
    data object NavigateToCreateAccount : SignInUiEffect
    data object NavigateToHome : SignInUiEffect
}

// TODO(DATA): keep the state/effect contract, but move coroutine launching to viewModelScope
// and inject a real repository-backed AuthUseCases implementation in the data stage.
class SignInViewModel(
    private val useCases: AuthUseCases,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    var uiState by mutableStateOf(SignInUiState())
        private set

    var uiEffect by mutableStateOf<SignInUiEffect?>(null)
        private set

    fun onEvent(event: SignInUiEvent) {
        when (event) {
            is SignInUiEvent.EmailChanged -> uiState = uiState.copy(email = event.value)
            is SignInUiEvent.PasswordChanged -> uiState = uiState.copy(password = event.value)
            SignInUiEvent.PasswordVisibilityToggled -> {
                uiState = uiState.copy(isPasswordVisible = !uiState.isPasswordVisible)
            }
            SignInUiEvent.ForgotPasswordClicked -> uiEffect = SignInUiEffect.NavigateToForgotPassword
            SignInUiEvent.CreateAccountClicked -> uiEffect = SignInUiEffect.NavigateToCreateAccount
            SignInUiEvent.DialogDismissed -> uiState = uiState.copy(dialogMessage = null)
            SignInUiEvent.SignInClicked -> scope.launch {
                uiState = uiState.copy(isLoading = true, dialogMessage = null)
                when (val result = useCases.signInWithEmail(uiState.email, uiState.password)) {
                    is AuthResult.Error -> {
                        uiState = uiState.copy(isLoading = false, dialogMessage = result.message)
                    }

                    is AuthResult.Success -> {
                        uiState = uiState.copy(isLoading = false)
                        uiEffect = SignInUiEffect.NavigateToHome
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
fun SignInRoute(
    viewModel: SignInViewModel = remember { SignInViewModel(AppContainer.authUseCases) },
    onForgotPassword: () -> Unit,
    onCreateAccount: () -> Unit,
    onSignInSuccess: () -> Unit,
    onBackClick: () -> Unit = {},
) {
    // Navigation is driven only by UiEffect; the screen itself stays free from nav logic.
    val effect = viewModel.uiEffect
    LaunchedEffect(effect) {
        when (effect) {
            SignInUiEffect.NavigateToCreateAccount -> {
                onCreateAccount()
                viewModel.consumeEffect()
            }

            SignInUiEffect.NavigateToForgotPassword -> {
                onForgotPassword()
                viewModel.consumeEffect()
            }

            SignInUiEffect.NavigateToHome -> {
                onSignInSuccess()
                viewModel.consumeEffect()
            }

            null -> Unit
        }
    }

    SignInScreen(
        state = viewModel.uiState,
        onEvent = viewModel::onEvent,
        onBackClick = onBackClick,
    )
}

@Composable
fun SignInScreen(
    state: SignInUiState,
    onEvent: (SignInUiEvent) -> Unit,
    onBackClick: () -> Unit,
) {
    AuthScreenScaffold(
        title = "Привет!",
        subtitle = "Заполните Свои Данные",
        onBackClick = onBackClick,
        bottomContent = {
            AuthBottomLink(
                prefix = "Вы впервые? ",
                action = "Создать",
                onClick = { onEvent(SignInUiEvent.CreateAccountClicked) },
            )
        },
    ) {
        AppTextField(
            value = state.email,
            onValueChange = { onEvent(SignInUiEvent.EmailChanged(it)) },
            label = "Email",
            placeholder = "xyz@gmail.com",
        )
        Spacer(modifier = Modifier.height(20.dp))
        PasswordTextField(
            value = state.password,
            onValueChange = { onEvent(SignInUiEvent.PasswordChanged(it)) },
            label = "Пароль",
            placeholder = "********",
            isPasswordVisible = state.isPasswordVisible,
            onVisibilityToggle = { onEvent(SignInUiEvent.PasswordVisibilityToggled) },
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center,
        ) {
            InlineActionText(
                text = "Восстановить",
                onClick = { onEvent(SignInUiEvent.ForgotPasswordClicked) },
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton(
            text = "Войти",
            onClick = { onEvent(SignInUiEvent.SignInClicked) },
            loading = state.isLoading,
        )
    }

    state.dialogMessage?.let {
        InfoDialog(
            title = "Ошибка",
            message = it,
            onDismiss = { onEvent(SignInUiEvent.DialogDismissed) },
        )
    }
}

@Preview
@Composable
private fun SignInScreenPreview() {
    AppTheme {
        SignInScreen(
            state = SignInUiState(email = "", password = ""),
            onEvent = {},
            onBackClick = {},
        )
    }
}
