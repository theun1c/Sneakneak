package com.example.sneakneak.domain.auth.validator

import com.example.sneakneak.domain.auth.model.ValidationResult

private val emailRegex = Regex("^[a-z0-9]+@[a-z0-9]+\\.[a-z]{2,}$")

object EmailValidator {
    fun validate(email: String): ValidationResult {
        val normalized = email.trim().lowercase()
        return when {
            normalized.isEmpty() -> ValidationResult.Invalid("Введите email")
            !emailRegex.matches(normalized) -> ValidationResult.Invalid("Введите корректный email")
            else -> ValidationResult.Valid
        }
    }
}

object PasswordValidator {
    fun validateForSignIn(password: String): ValidationResult {
        return if (password.isBlank()) {
            ValidationResult.Invalid("Введите пароль")
        } else {
            ValidationResult.Valid
        }
    }

    fun validateForSignUp(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Invalid("Введите пароль")
            password.length < 8 -> ValidationResult.Invalid("Пароль должен быть не короче 8 символов")
            else -> ValidationResult.Valid
        }
    }
}

object ConfirmPasswordValidator {
    fun validate(password: String, confirmPassword: String): ValidationResult {
        return when {
            confirmPassword.isBlank() -> ValidationResult.Invalid("Подтвердите пароль")
            password != confirmPassword -> ValidationResult.Invalid("Пароли не совпадают")
            else -> ValidationResult.Valid
        }
    }
}

object OtpCodeValidator {
    fun validate(code: String): ValidationResult {
        return if (code.length == 6 && code.all(Char::isDigit)) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid("Введите 6-значный код")
        }
    }
}
