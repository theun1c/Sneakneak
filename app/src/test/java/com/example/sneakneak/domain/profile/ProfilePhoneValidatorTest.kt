package com.example.sneakneak.domain.profile

import com.example.sneakneak.domain.profile.validator.ProfilePhoneValidationResult
import com.example.sneakneak.domain.profile.validator.ProfilePhoneValidator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfilePhoneValidatorTest {

    @Test
    fun `blank phone is valid`() {
        val result = ProfilePhoneValidator.validate("   ")

        assertTrue(result is ProfilePhoneValidationResult.Valid)
    }

    @Test
    fun `formatted russian phone is valid`() {
        val result = ProfilePhoneValidator.validate("+7 (911) 123-45-67")

        assertTrue(result is ProfilePhoneValidationResult.Valid)
    }

    @Test
    fun `letters are invalid`() {
        val result = ProfilePhoneValidator.validate("abc")

        assertEquals(
            ProfilePhoneValidationResult.Invalid(ProfilePhoneValidator.INVALID_PHONE_MESSAGE),
            result,
        )
    }

    @Test
    fun `plus sign not at start is invalid`() {
        val result = ProfilePhoneValidator.validate("79+11234567")

        assertEquals(
            ProfilePhoneValidationResult.Invalid(ProfilePhoneValidator.INVALID_PHONE_MESSAGE),
            result,
        )
    }

    @Test
    fun `too short phone is invalid`() {
        val result = ProfilePhoneValidator.validate("12345")

        assertEquals(
            ProfilePhoneValidationResult.Invalid(ProfilePhoneValidator.INVALID_PHONE_MESSAGE),
            result,
        )
    }
}
