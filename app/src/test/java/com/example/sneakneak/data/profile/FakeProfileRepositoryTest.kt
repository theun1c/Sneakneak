package com.example.sneakneak.data.profile

import com.example.sneakneak.data.auth.FakeAuthRepository
import com.example.sneakneak.domain.profile.model.ProfileResult
import com.example.sneakneak.domain.profile.model.UserProfileDraft
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FakeProfileRepositoryTest {

    @Test
    fun create_or_get_profile_uses_current_auth_user() = runBlocking {
        val authRepository = FakeAuthRepository()
        authRepository.signIn("demo@sneakneak.com", "password123")
        val repository = FakeProfileRepository(authRepository)

        val result = repository.createOrGetMyProfile(nameHint = "Emmanuel Oyiboke")

        assertTrue(result is ProfileResult.Success)
        val profile = (result as ProfileResult.Success).data
        assertEquals("Emmanuel", profile.firstname)
        assertEquals("Oyiboke", profile.lastname)
    }

    @Test
    fun upsert_profile_updates_saved_values() = runBlocking {
        val authRepository = FakeAuthRepository()
        authRepository.signIn("demo@sneakneak.com", "password123")
        val repository = FakeProfileRepository(authRepository)
        repository.createOrGetMyProfile(nameHint = null)

        val result = repository.upsertMyProfile(
            UserProfileDraft(
                firstname = "Test",
                lastname = "User",
                address = "Moscow",
                phone = "+79990001122",
                photo = null,
            ),
        )

        assertTrue(result is ProfileResult.Success)
        val profile = (result as ProfileResult.Success).data
        assertEquals("Test", profile.firstname)
        assertEquals("User", profile.lastname)
        assertEquals("Moscow", profile.address)
    }
}

