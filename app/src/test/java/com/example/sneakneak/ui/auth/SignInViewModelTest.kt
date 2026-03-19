package com.example.sneakneak.ui.auth

import com.example.sneakneak.data.auth.FakeAuthRepository
import com.example.sneakneak.domain.auth.usecase.AuthUseCases
import com.example.sneakneak.domain.auth.usecase.GetCurrentUserEmailUseCase
import com.example.sneakneak.domain.auth.usecase.GetCurrentUserIdUseCase
import com.example.sneakneak.domain.auth.usecase.ObserveSessionUseCase
import com.example.sneakneak.domain.auth.usecase.SendRecoveryCodeUseCase
import com.example.sneakneak.domain.auth.usecase.SignInWithEmailUseCase
import com.example.sneakneak.domain.auth.usecase.SignOutUseCase
import com.example.sneakneak.domain.auth.usecase.SignUpWithEmailUseCase
import com.example.sneakneak.domain.auth.usecase.UpdatePasswordUseCase
import com.example.sneakneak.domain.auth.usecase.VerifyRecoveryCodeUseCase
import com.example.sneakneak.ui.auth.signin.SignInUiEffect
import com.example.sneakneak.ui.auth.signin.SignInUiEvent
import com.example.sneakneak.ui.auth.signin.SignInViewModel
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SignInViewModelTest {

    private fun authUseCases(): AuthUseCases {
        val repository = FakeAuthRepository()
        return AuthUseCases(
            signInWithEmail = SignInWithEmailUseCase(repository),
            signUpWithEmail = SignUpWithEmailUseCase(repository),
            sendRecoveryCode = SendRecoveryCodeUseCase(repository),
            verifyRecoveryCode = VerifyRecoveryCodeUseCase(repository),
            updatePassword = UpdatePasswordUseCase(repository),
            observeSession = ObserveSessionUseCase(repository),
            signOut = SignOutUseCase(repository),
            getCurrentUserId = GetCurrentUserIdUseCase(repository),
            getCurrentUserEmail = GetCurrentUserEmailUseCase(repository),
        )
    }

    @Test
    fun sign_in_with_empty_fields_shows_dialog() {
        val viewModel = SignInViewModel(authUseCases(), Dispatchers.Unconfined)

        viewModel.onEvent(SignInUiEvent.SignInClicked)

        assertTrue(viewModel.uiState.dialogMessage != null)
    }

    @Test
    fun forgot_password_click_emits_navigation_effect() {
        val viewModel = SignInViewModel(authUseCases(), Dispatchers.Unconfined)

        viewModel.onEvent(SignInUiEvent.ForgotPasswordClicked)

        assertEquals(SignInUiEffect.NavigateToForgotPassword, viewModel.uiEffect)
    }
}
