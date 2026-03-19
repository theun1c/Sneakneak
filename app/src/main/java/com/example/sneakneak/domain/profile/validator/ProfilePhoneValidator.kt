package com.example.sneakneak.domain.profile.validator

// Валидатор телефона для profile/edit profile.
// Разрешает распространенные символы форматирования и проверяет диапазон цифр.
sealed interface ProfilePhoneValidationResult {
    data object Valid : ProfilePhoneValidationResult
    data class Invalid(val message: String) : ProfilePhoneValidationResult
}

object ProfilePhoneValidator {
    const val INVALID_PHONE_MESSAGE = "Введите корректный номер телефона"

    fun validate(phone: String): ProfilePhoneValidationResult {
        val value = phone.trim()
        if (value.isBlank()) return ProfilePhoneValidationResult.Valid

        if (value.count { it == '+' } > 1) {
            return ProfilePhoneValidationResult.Invalid(INVALID_PHONE_MESSAGE)
        }
        if (value.contains('+') && !value.startsWith("+")) {
            return ProfilePhoneValidationResult.Invalid(INVALID_PHONE_MESSAGE)
        }

        val containsOnlyAllowedChars = value.all { char ->
            char.isDigit() || char == '+' || char == ' ' || char == '-' || char == '(' || char == ')'
        }
        if (!containsOnlyAllowedChars) {
            return ProfilePhoneValidationResult.Invalid(INVALID_PHONE_MESSAGE)
        }

        val digitsCount = value.count(Char::isDigit)
        if (digitsCount !in 7..15) {
            return ProfilePhoneValidationResult.Invalid(INVALID_PHONE_MESSAGE)
        }

        return ProfilePhoneValidationResult.Valid
    }
}
