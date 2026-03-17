package com.example.sneakneak.data.auth

import com.example.sneakneak.domain.auth.model.AuthResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class FakeAuthRepositoryTest {

    @Test
    fun sign_in_with_seeded_user_creates_session() = runBlocking {
        val repository = FakeAuthRepository()

        val result = repository.signIn(
            email = "demo@sneakneak.com",
            password = "password123",
        )

        assert(result is AuthResult.Success)
        assertNotNull(repository.observeSession().value)
    }

    @Test
    fun recovery_code_verification_and_password_update_allow_new_sign_in() = runBlocking {
        val repository = FakeAuthRepository()

        repository.sendRecoveryCode("demo@sneakneak.com")
        repository.verifyRecoveryCode("demo@sneakneak.com", FakeAuthRepository.DEFAULT_RECOVERY_CODE)
        repository.updatePassword("demo@sneakneak.com", "newpassword123")
        val result = repository.signIn("demo@sneakneak.com", "newpassword123")

        assert(result is AuthResult.Success)
    }

    @Test
    fun sign_out_clears_active_session() = runBlocking {
        val repository = FakeAuthRepository()
        repository.signIn("demo@sneakneak.com", "password123")

        val result = repository.signOut()

        assertEquals(AuthResult.Success(Unit), result)
        assertNull(repository.observeSession().value)
    }
}
