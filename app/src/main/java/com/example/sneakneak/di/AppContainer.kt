package com.example.sneakneak.di

import com.example.sneakneak.data.auth.FakeAuthRepository
import com.example.sneakneak.domain.auth.usecase.AuthUseCases
import com.example.sneakneak.domain.auth.usecase.ObserveSessionUseCase
import com.example.sneakneak.domain.auth.usecase.SendRecoveryCodeUseCase
import com.example.sneakneak.domain.auth.usecase.SignInWithEmailUseCase
import com.example.sneakneak.domain.auth.usecase.SignOutUseCase
import com.example.sneakneak.domain.auth.usecase.SignUpWithEmailUseCase
import com.example.sneakneak.domain.auth.usecase.UpdatePasswordUseCase
import com.example.sneakneak.domain.auth.usecase.VerifyRecoveryCodeUseCase

// Minimal manual composition root for the current stage.
// This keeps screen code free from direct repository construction until real DI is introduced.
object AppContainer {
    private val authRepository = FakeAuthRepository()

    val authUseCases: AuthUseCases by lazy {
        // TODO(DATA): swap FakeAuthRepository for real data/repository bindings without changing UI contracts.
        AuthUseCases(
            signInWithEmail = SignInWithEmailUseCase(authRepository),
            signUpWithEmail = SignUpWithEmailUseCase(authRepository),
            sendRecoveryCode = SendRecoveryCodeUseCase(authRepository),
            verifyRecoveryCode = VerifyRecoveryCodeUseCase(authRepository),
            updatePassword = UpdatePasswordUseCase(authRepository),
            observeSession = ObserveSessionUseCase(authRepository),
            signOut = SignOutUseCase(authRepository),
        )
    }
}
