package com.example.sneakneak.domain.auth

import com.example.sneakneak.domain.auth.model.ValidationResult
import com.example.sneakneak.domain.auth.validator.ConfirmPasswordValidator
import com.example.sneakneak.domain.auth.validator.EmailValidator
import com.example.sneakneak.domain.auth.validator.OtpCodeValidator
import com.example.sneakneak.domain.auth.validator.PasswordValidator
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthValidatorsTest {

    @Test
    fun invalid_email_returns_error() {
        val result = EmailValidator.validate("not-an-email")

        assertEquals(
            ValidationResult.Invalid("Введите корректный email"),
            result,
        )
    }

    @Test
    fun short_signup_password_returns_error() {
        val result = PasswordValidator.validateForSignUp("1234567")

        assertEquals(
            ValidationResult.Invalid("Пароль должен быть не короче 8 символов"),
            result,
        )
    }

    @Test
    fun mismatched_confirm_password_returns_error() {
        val result = ConfirmPasswordValidator.validate("password123", "password321")

        assertEquals(
            ValidationResult.Invalid("Пароли не совпадают"),
            result,
        )
    }

    @Test
    fun otp_with_non_digit_or_wrong_length_returns_error() {
        val result = OtpCodeValidator.validate("12ab")

        assertEquals(
            ValidationResult.Invalid("Введите 6-значный код"),
            result,
        )
    }
}
