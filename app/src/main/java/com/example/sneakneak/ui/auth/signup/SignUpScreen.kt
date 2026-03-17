package com.example.sneakneak.ui.auth.signup

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
import com.example.sneakneak.ui.auth.common.AuthBottomLink
import com.example.sneakneak.ui.auth.common.AuthScreenScaffold
import com.example.sneakneak.ui.auth.common.ConsentRow
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

enum class SignUpVisualState {
    Default,
    ConsentChecked,
}

// Registration currently mirrors both design states from the bundled reference assets:
// disabled CTA until all required fields are present and consent is checked.
data class SignUpUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val consentChecked: Boolean = false,
    val visualState: SignUpVisualState = SignUpVisualState.Default,
    val isLoading: Boolean = false,
    val dialogMessage: String? = null,
    val isSubmitEnabled: Boolean = false,
)

sealed interface SignUpUiEvent {
    data class NameChanged(val value: String) : SignUpUiEvent
    data class EmailChanged(val value: String) : SignUpUiEvent
    data class PasswordChanged(val value: String) : SignUpUiEvent
    data class ConsentChanged(val value: Boolean) : SignUpUiEvent
    data object PasswordVisibilityToggled : SignUpUiEvent
    data object SignUpClicked : SignUpUiEvent
    data object SignInClicked : SignUpUiEvent
    data object BackClicked : SignUpUiEvent
    data object DialogDismissed : SignUpUiEvent
}

sealed interface SignUpUiEffect {
    data object NavigateBack : SignUpUiEffect
    data object NavigateToSignIn : SignUpUiEffect
}

// TODO(DATA): replace mock registration result with repository-backed sign-up while preserving
// the same button enablement and dialog/navigation contract.
class SignUpViewModel(
    private val useCases: AuthUseCases,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    var uiState by mutableStateOf(SignUpUiState())
        private set

    var uiEffect by mutableStateOf<SignUpUiEffect?>(null)
        private set

    private fun syncSubmitState() {
        uiState = uiState.copy(
            isSubmitEnabled = uiState.name.isNotBlank() &&
                uiState.email.isNotBlank() &&
                uiState.password.isNotBlank() &&
                uiState.consentChecked,
        )
    }

    fun onEvent(event: SignUpUiEvent) {
        when (event) {
            is SignUpUiEvent.NameChanged -> {
                uiState = uiState.copy(name = event.value)
                syncSubmitState()
            }
            is SignUpUiEvent.EmailChanged -> {
                uiState = uiState.copy(email = event.value)
                syncSubmitState()
            }
            is SignUpUiEvent.PasswordChanged -> {
                uiState = uiState.copy(password = event.value)
                syncSubmitState()
            }
            SignUpUiEvent.PasswordVisibilityToggled -> {
                uiState = uiState.copy(isPasswordVisible = !uiState.isPasswordVisible)
            }
            is SignUpUiEvent.ConsentChanged -> {
                uiState = uiState.copy(
                    consentChecked = event.value,
                    visualState = if (event.value) SignUpVisualState.ConsentChecked else SignUpVisualState.Default,
                )
                syncSubmitState()
            }

            SignUpUiEvent.SignUpClicked -> scope.launch {
                if (!uiState.isSubmitEnabled) return@launch
                uiState = uiState.copy(isLoading = true, dialogMessage = null)
                when (val result = useCases.signUpWithEmail(uiState.name, uiState.email, uiState.password)) {
                    is AuthResult.Error -> {
                        uiState = uiState.copy(isLoading = false, dialogMessage = result.message)
                    }

                    is AuthResult.Success -> {
                        uiState = uiState.copy(isLoading = false)
                        uiEffect = SignUpUiEffect.NavigateToSignIn
                    }
                }
            }

            SignUpUiEvent.SignInClicked -> uiEffect = SignUpUiEffect.NavigateToSignIn
            SignUpUiEvent.BackClicked -> uiEffect = SignUpUiEffect.NavigateBack
            SignUpUiEvent.DialogDismissed -> uiState = uiState.copy(dialogMessage = null)
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
fun SignUpRoute(
    viewModel: SignUpViewModel = remember { SignUpViewModel(AppContainer.authUseCases) },
    onBack: () -> Unit,
    onNavigateToSignIn: () -> Unit,
) {
    val effect = viewModel.uiEffect
    LaunchedEffect(effect) {
        when (effect) {
            SignUpUiEffect.NavigateBack -> {
                onBack()
                viewModel.consumeEffect()
            }

            SignUpUiEffect.NavigateToSignIn -> {
                onNavigateToSignIn()
                viewModel.consumeEffect()
            }

            null -> Unit
        }
    }

    SignUpScreen(
        state = viewModel.uiState,
        onEvent = viewModel::onEvent,
    )
}

@Composable
fun SignUpScreen(
    state: SignUpUiState,
    onEvent: (SignUpUiEvent) -> Unit,
) {
    AuthScreenScaffold(
        title = "Регистрация",
        subtitle = "Заполните Свои Данные",
        onBackClick = { onEvent(SignUpUiEvent.BackClicked) },
        bottomContent = {
            AuthBottomLink(
                prefix = "Есть аккаунт? ",
                action = "Войти",
                onClick = { onEvent(SignUpUiEvent.SignInClicked) },
            )
        },
    ) {
        AppTextField(
            value = state.name,
            onValueChange = { onEvent(SignUpUiEvent.NameChanged(it)) },
            label = "Ваше имя",
            placeholder = "xxxxxxxx",
        )
        Spacer(modifier = Modifier.height(20.dp))
        AppTextField(
            value = state.email,
            onValueChange = { onEvent(SignUpUiEvent.EmailChanged(it)) },
            label = "Email",
            placeholder = "xyz@gmail.com",
        )
        Spacer(modifier = Modifier.height(20.dp))
        PasswordTextField(
            value = state.password,
            onValueChange = { onEvent(SignUpUiEvent.PasswordChanged(it)) },
            label = "Пароль",
            placeholder = "********",
            isPasswordVisible = state.isPasswordVisible,
            onVisibilityToggle = { onEvent(SignUpUiEvent.PasswordVisibilityToggled) },
        )
        Spacer(modifier = Modifier.height(20.dp))
        ConsentRow(
            checked = state.consentChecked,
            text = "Даю согласие на обработку\nперсональных данных",
            onCheckedChange = { onEvent(SignUpUiEvent.ConsentChanged(it)) },
        )
        Spacer(modifier = Modifier.height(20.dp))
        PrimaryButton(
            text = "Зарегистрироваться",
            onClick = { onEvent(SignUpUiEvent.SignUpClicked) },
            enabled = state.isSubmitEnabled,
            loading = state.isLoading,
        )
    }

    state.dialogMessage?.let {
        InfoDialog(
            title = "Ошибка",
            message = it,
            onDismiss = { onEvent(SignUpUiEvent.DialogDismissed) },
        )
    }
}

@Preview
@Composable
private fun SignUpScreenPreview() {
    AppTheme {
        SignUpScreen(
            state = SignUpUiState(),
            onEvent = {},
        )
    }
}

@Preview
@Composable
private fun SignUpAlternatePreview() {
    AppTheme {
        SignUpScreen(
            state = SignUpUiState(
                consentChecked = true,
                visualState = SignUpVisualState.ConsentChecked,
            ),
            onEvent = {},
        )
    }
}
