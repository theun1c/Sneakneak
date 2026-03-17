package com.example.sneakneak.ui.auth

import com.example.sneakneak.data.auth.FakeAuthRepository
import com.example.sneakneak.domain.auth.usecase.AuthUseCases
import com.example.sneakneak.domain.auth.usecase.ObserveSessionUseCase
import com.example.sneakneak.domain.auth.usecase.SendRecoveryCodeUseCase
import com.example.sneakneak.domain.auth.usecase.SignInWithEmailUseCase
import com.example.sneakneak.domain.auth.usecase.SignOutUseCase
import com.example.sneakneak.domain.auth.usecase.SignUpWithEmailUseCase
import com.example.sneakneak.domain.auth.usecase.UpdatePasswordUseCase
import com.example.sneakneak.domain.auth.usecase.VerifyRecoveryCodeUseCase
import com.example.sneakneak.ui.auth.forgot.ForgotPasswordUiEffect
import com.example.sneakneak.ui.auth.forgot.ForgotPasswordUiEvent
import com.example.sneakneak.ui.auth.forgot.ForgotPasswordViewModel
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ForgotPasswordViewModelTest {

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
        )
    }

    @Test
    fun send_click_with_email_opens_success_dialog() {
        val viewModel = ForgotPasswordViewModel(authUseCases(), Dispatchers.Unconfined)

        viewModel.onEvent(ForgotPasswordUiEvent.EmailChanged("demo@sneakneak.com"))
        viewModel.onEvent(ForgotPasswordUiEvent.SendClicked)

        assertTrue(viewModel.uiState.showSuccessDialog)
    }

    @Test
    fun success_dialog_confirmation_emits_otp_navigation() {
        val viewModel = ForgotPasswordViewModel(authUseCases(), Dispatchers.Unconfined)

        viewModel.onEvent(ForgotPasswordUiEvent.EmailChanged("demo@sneakneak.com"))
        viewModel.onEvent(ForgotPasswordUiEvent.SendClicked)
        viewModel.onEvent(ForgotPasswordUiEvent.SuccessDialogConfirmed)

        assertFalse(viewModel.uiState.showSuccessDialog)
        assertEquals(
            ForgotPasswordUiEffect.NavigateToOtp("demo@sneakneak.com"),
            viewModel.uiEffect,
        )
    }
}
