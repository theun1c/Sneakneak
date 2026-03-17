package com.example.sneakneak.domain.auth.usecase

import com.example.sneakneak.domain.auth.model.AuthResult
import com.example.sneakneak.domain.auth.model.AuthSession
import com.example.sneakneak.domain.auth.model.ValidationResult
import com.example.sneakneak.domain.auth.repository.AuthRepository
import com.example.sneakneak.domain.auth.validator.ConfirmPasswordValidator
import com.example.sneakneak.domain.auth.validator.EmailValidator
import com.example.sneakneak.domain.auth.validator.OtpCodeValidator
import com.example.sneakneak.domain.auth.validator.PasswordValidator
import kotlinx.coroutines.flow.StateFlow

class SignInWithEmailUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String, password: String): AuthResult<AuthSession> {
        when (val result = EmailValidator.validate(email)) {
            is ValidationResult.Invalid -> return AuthResult.Error(result.message)
            ValidationResult.Valid -> Unit
        }
        when (val result = PasswordValidator.validateForSignIn(password)) {
            is ValidationResult.Invalid -> return AuthResult.Error(result.message)
            ValidationResult.Valid -> Unit
        }
        return repository.signIn(email.trim().lowercase(), password)
    }
}

class SignUpWithEmailUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(name: String, email: String, password: String): AuthResult<Unit> {
        when (val result = EmailValidator.validate(email)) {
            is ValidationResult.Invalid -> return AuthResult.Error(result.message)
            ValidationResult.Valid -> Unit
        }
        when (val result = PasswordValidator.validateForSignUp(password)) {
            is ValidationResult.Invalid -> return AuthResult.Error(result.message)
            ValidationResult.Valid -> Unit
        }
        return repository.signUp(name = name.trim(), email = email.trim().lowercase(), password = password)
    }
}

class SendRecoveryCodeUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String): AuthResult<Unit> {
        when (val result = EmailValidator.validate(email)) {
            is ValidationResult.Invalid -> return AuthResult.Error(result.message)
            ValidationResult.Valid -> Unit
        }
        return repository.sendRecoveryCode(email.trim().lowercase())
    }
}

class VerifyRecoveryCodeUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(email: String, code: String): AuthResult<Unit> {
        when (val result = EmailValidator.validate(email)) {
            is ValidationResult.Invalid -> return AuthResult.Error(result.message)
            ValidationResult.Valid -> Unit
        }
        when (val result = OtpCodeValidator.validate(code)) {
            is ValidationResult.Invalid -> return AuthResult.Error(result.message)
            ValidationResult.Valid -> Unit
        }
        return repository.verifyRecoveryCode(email.trim().lowercase(), code)
    }
}

class UpdatePasswordUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String,
    ): AuthResult<Unit> {
        when (val result = EmailValidator.validate(email)) {
            is ValidationResult.Invalid -> return AuthResult.Error(result.message)
            ValidationResult.Valid -> Unit
        }
        when (val result = PasswordValidator.validateForSignUp(password)) {
            is ValidationResult.Invalid -> return AuthResult.Error(result.message)
            ValidationResult.Valid -> Unit
        }
        when (val result = ConfirmPasswordValidator.validate(password, confirmPassword)) {
            is ValidationResult.Invalid -> return AuthResult.Error(result.message)
            ValidationResult.Valid -> Unit
        }
        return repository.updatePassword(email.trim().lowercase(), password)
    }
}

class ObserveSessionUseCase(
    private val repository: AuthRepository,
) {
    operator fun invoke(): StateFlow<AuthSession?> = repository.observeSession()
}

class SignOutUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): AuthResult<Unit> = repository.signOut()
}

data class AuthUseCases(
    val signInWithEmail: SignInWithEmailUseCase,
    val signUpWithEmail: SignUpWithEmailUseCase,
    val sendRecoveryCode: SendRecoveryCodeUseCase,
    val verifyRecoveryCode: VerifyRecoveryCodeUseCase,
    val updatePassword: UpdatePasswordUseCase,
    val observeSession: ObserveSessionUseCase,
    val signOut: SignOutUseCase,
)
