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

// Набор use case для auth-flow.
// Здесь живут бизнес-валидации и нормализация входа перед передачей в repository.
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
        // UI регистрации содержит имя, хотя auth в Supabase основан на email/password.
        // name передаем как `nameHint`, а фактическое хранение выполняется в profile bootstrap.
        if (name.isBlank()) {
            return AuthResult.Error("Введите имя")
        }
        when (val result = EmailValidator.validate(email)) {
            is ValidationResult.Invalid -> return AuthResult.Error(result.message)
            ValidationResult.Valid -> Unit
        }
        when (val result = PasswordValidator.validateForSignUp(password)) {
            is ValidationResult.Invalid -> return AuthResult.Error(result.message)
            ValidationResult.Valid -> Unit
        }
        return repository.signUp(
            email = email.trim().lowercase(),
            password = password,
            nameHint = name.trim(),
        )
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
        password: String,
        confirmPassword: String,
    ): AuthResult<Unit> {
        when (val result = PasswordValidator.validateForSignUp(password)) {
            is ValidationResult.Invalid -> return AuthResult.Error(result.message)
            ValidationResult.Valid -> Unit
        }
        when (val result = ConfirmPasswordValidator.validate(password, confirmPassword)) {
            is ValidationResult.Invalid -> return AuthResult.Error(result.message)
            ValidationResult.Valid -> Unit
        }
        return repository.updatePassword(password)
    }
}

class ObserveSessionUseCase(
    private val repository: AuthRepository,
) {
    // Используется Splash и навигацией для выбора auth/main графа.
    operator fun invoke(): StateFlow<AuthSession?> = repository.observeSession()
}

class SignOutUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): AuthResult<Unit> = repository.signOut()
}

class GetCurrentUserIdUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): String? = repository.getCurrentUserId()
}

class GetCurrentUserEmailUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): String? = repository.getCurrentUserEmail()
}

data class AuthUseCases(
    val signInWithEmail: SignInWithEmailUseCase,
    val signUpWithEmail: SignUpWithEmailUseCase,
    val sendRecoveryCode: SendRecoveryCodeUseCase,
    val verifyRecoveryCode: VerifyRecoveryCodeUseCase,
    val updatePassword: UpdatePasswordUseCase,
    val observeSession: ObserveSessionUseCase,
    val signOut: SignOutUseCase,
    val getCurrentUserId: GetCurrentUserIdUseCase,
    val getCurrentUserEmail: GetCurrentUserEmailUseCase,
)
