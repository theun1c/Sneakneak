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
import com.example.sneakneak.ui.auth.forgot.ForgotPasswordUiEvent
import com.example.sneakneak.ui.auth.forgot.ForgotPasswordUiEffect
import com.example.sneakneak.ui.auth.forgot.ForgotPasswordViewModel
import com.example.sneakneak.ui.auth.newpassword.NewPasswordUiEffect
import com.example.sneakneak.ui.auth.newpassword.NewPasswordUiEvent
import com.example.sneakneak.ui.auth.newpassword.NewPasswordViewModel
import com.example.sneakneak.ui.auth.otp.OtpUiEffect
import com.example.sneakneak.ui.auth.otp.OtpUiEvent
import com.example.sneakneak.ui.auth.otp.OtpViewModel
import com.example.sneakneak.ui.auth.signin.SignInUiEffect
import com.example.sneakneak.ui.auth.signin.SignInUiEvent
import com.example.sneakneak.ui.auth.signin.SignInViewModel
import com.example.sneakneak.ui.auth.signup.SignUpUiEffect
import com.example.sneakneak.ui.auth.signup.SignUpUiEvent
import com.example.sneakneak.ui.auth.signup.SignUpViewModel
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AuthFlowViewModelTest {

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
    fun sign_in_with_seeded_credentials_emits_home_navigation() {
        val viewModel = SignInViewModel(authUseCases(), Dispatchers.Unconfined)

        viewModel.onEvent(SignInUiEvent.EmailChanged("demo@sneakneak.com"))
        viewModel.onEvent(SignInUiEvent.PasswordChanged("password123"))
        viewModel.onEvent(SignInUiEvent.SignInClicked)

        assertEquals(SignInUiEffect.NavigateToHome, viewModel.uiEffect)
        assertFalse(viewModel.uiState.isLoading)
    }

    @Test
    fun sign_up_with_valid_data_emits_sign_in_navigation() {
        val viewModel = SignUpViewModel(authUseCases(), Dispatchers.Unconfined)

        viewModel.onEvent(SignUpUiEvent.NameChanged("New User"))
        viewModel.onEvent(SignUpUiEvent.EmailChanged("newuser@test.com"))
        viewModel.onEvent(SignUpUiEvent.PasswordChanged("password123"))
        viewModel.onEvent(SignUpUiEvent.ConsentChanged(true))
        viewModel.onEvent(SignUpUiEvent.SignUpClicked)

        assertEquals(SignUpUiEffect.NavigateToSignIn, viewModel.uiEffect)
    }

    @Test
    fun sign_up_button_stays_disabled_until_all_fields_and_consent_are_ready() {
        val viewModel = SignUpViewModel(authUseCases(), Dispatchers.Unconfined)

        assertFalse(viewModel.uiState.isSubmitEnabled)

        viewModel.onEvent(SignUpUiEvent.NameChanged("New User"))
        viewModel.onEvent(SignUpUiEvent.EmailChanged("newuser@test.com"))
        viewModel.onEvent(SignUpUiEvent.PasswordChanged("password123"))

        assertFalse(viewModel.uiState.isSubmitEnabled)

        viewModel.onEvent(SignUpUiEvent.ConsentChanged(true))

        assertTrue(viewModel.uiState.isSubmitEnabled)
    }

    @Test
    fun sign_up_click_does_nothing_while_button_is_disabled() {
        val viewModel = SignUpViewModel(authUseCases(), Dispatchers.Unconfined)

        viewModel.onEvent(SignUpUiEvent.NameChanged("New User"))
        viewModel.onEvent(SignUpUiEvent.EmailChanged("newuser@test.com"))
        viewModel.onEvent(SignUpUiEvent.PasswordChanged("password123"))
        viewModel.onEvent(SignUpUiEvent.SignUpClicked)

        assertEquals(null, viewModel.uiEffect)
        assertFalse(viewModel.uiState.isLoading)
    }

    @Test
    fun forgot_password_with_known_email_opens_success_dialog() {
        val viewModel = ForgotPasswordViewModel(authUseCases(), Dispatchers.Unconfined)

        viewModel.onEvent(ForgotPasswordUiEvent.EmailChanged("demo@sneakneak.com"))
        viewModel.onEvent(ForgotPasswordUiEvent.SendClicked)

        assertTrue(viewModel.uiState.showSuccessDialog)
        assertFalse(viewModel.uiState.isLoading)
    }

    @Test
    fun otp_with_correct_code_navigates_to_new_password() {
        val useCases = authUseCases()
        val forgotViewModel = ForgotPasswordViewModel(useCases, Dispatchers.Unconfined)
        forgotViewModel.onEvent(ForgotPasswordUiEvent.EmailChanged("demo@sneakneak.com"))
        forgotViewModel.onEvent(ForgotPasswordUiEvent.SendClicked)

        val viewModel = OtpViewModel("demo@sneakneak.com", useCases, Dispatchers.Unconfined)
        viewModel.onEvent(OtpUiEvent.CodeChanged(FakeAuthRepository.DEFAULT_RECOVERY_CODE))
        viewModel.onEvent(OtpUiEvent.ContinueClicked)

        assertEquals(
            OtpUiEffect.NavigateToNewPassword("demo@sneakneak.com"),
            viewModel.uiEffect,
        )
    }

    @Test
    fun new_password_after_verified_code_navigates_back_to_sign_in() {
        val useCases = authUseCases()
        val forgotViewModel = ForgotPasswordViewModel(useCases, Dispatchers.Unconfined)
        forgotViewModel.onEvent(ForgotPasswordUiEvent.EmailChanged("demo@sneakneak.com"))
        forgotViewModel.onEvent(ForgotPasswordUiEvent.SendClicked)

        val otpViewModel = OtpViewModel("demo@sneakneak.com", useCases, Dispatchers.Unconfined)
        otpViewModel.onEvent(OtpUiEvent.CodeChanged(FakeAuthRepository.DEFAULT_RECOVERY_CODE))
        otpViewModel.onEvent(OtpUiEvent.ContinueClicked)

        val viewModel = NewPasswordViewModel(useCases, Dispatchers.Unconfined)
        viewModel.onEvent(NewPasswordUiEvent.PasswordChanged("updatedpass123"))
        viewModel.onEvent(NewPasswordUiEvent.ConfirmPasswordChanged("updatedpass123"))
        viewModel.onEvent(NewPasswordUiEvent.SaveClicked)

        assertEquals(NewPasswordUiEffect.NavigateToSignIn, viewModel.uiEffect)
        assertFalse(viewModel.uiState.isLoading)
    }

    @Test
    fun forgot_password_success_confirmation_emits_otp_navigation() {
        val viewModel = ForgotPasswordViewModel(authUseCases(), Dispatchers.Unconfined)

        viewModel.onEvent(ForgotPasswordUiEvent.EmailChanged("demo@sneakneak.com"))
        viewModel.onEvent(ForgotPasswordUiEvent.SendClicked)
        viewModel.onEvent(ForgotPasswordUiEvent.SuccessDialogConfirmed)

        assertEquals(
            ForgotPasswordUiEffect.NavigateToOtp("demo@sneakneak.com"),
            viewModel.uiEffect,
        )
    }
}
