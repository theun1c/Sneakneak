package com.example.sneakneak.data.profile

import com.example.sneakneak.domain.auth.repository.AuthRepository
import com.example.sneakneak.domain.profile.model.ProfileResult
import com.example.sneakneak.domain.profile.model.UserProfile
import com.example.sneakneak.domain.profile.model.UserProfileDraft
import com.example.sneakneak.domain.profile.repository.ProfileRepository

// Fake-репозиторий профиля для режима без backend.
// Поведение совместимо с реальным контрактом ProfileRepository, чтобы UI не зависел от источника данных.
class FakeProfileRepository(
    private val authRepository: AuthRepository,
) : ProfileRepository {
    private val profilesByUserId = mutableMapOf<String, UserProfile>()

    override suspend fun getMyProfile(): ProfileResult<UserProfile> {
        val userId = authRepository.getCurrentUserId()
            ?: return ProfileResult.Error("Сессия недействительна. Войдите снова")
        val email = authRepository.getCurrentUserEmail()
        val profile = profilesByUserId[userId]
            ?: return ProfileResult.Error("Профиль не найден")
        return ProfileResult.Success(profile.copy(email = email))
    }

    override suspend fun createOrGetMyProfile(nameHint: String?): ProfileResult<UserProfile> {
        val userId = authRepository.getCurrentUserId()
            ?: return ProfileResult.Error("Сессия недействительна. Войдите снова")
        val email = authRepository.getCurrentUserEmail()

        val existing = profilesByUserId[userId]
        if (existing != null) {
            return ProfileResult.Success(existing.copy(email = email))
        }

        val fallbackName = nameHint
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?: email?.substringBefore("@")?.takeIf { it.isNotBlank() }
            ?: "User"
        val words = fallbackName.split("\\s+".toRegex()).filter { it.isNotBlank() }
        val first = words.firstOrNull().orEmpty()
        val last = words.drop(1).joinToString(" ")
        val created = UserProfile(
            userId = userId,
            email = email,
            firstname = first,
            lastname = last,
            address = "",
            phone = "",
            photo = null,
        )
        profilesByUserId[userId] = created
        return ProfileResult.Success(created)
    }

    override suspend fun upsertMyProfile(draft: UserProfileDraft): ProfileResult<UserProfile> {
        val userId = authRepository.getCurrentUserId()
            ?: return ProfileResult.Error("Сессия недействительна. Войдите снова")
        val email = authRepository.getCurrentUserEmail()

        val updated = UserProfile(
            userId = userId,
            email = email,
            firstname = draft.firstname,
            lastname = draft.lastname,
            address = draft.address,
            phone = draft.phone,
            photo = draft.photo,
        )
        profilesByUserId[userId] = updated
        return ProfileResult.Success(updated)
    }

    override suspend fun updateMyProfile(draft: UserProfileDraft): ProfileResult<UserProfile> {
        return upsertMyProfile(draft)
    }

    override suspend fun updateMyAvatar(imageBytes: ByteArray): ProfileResult<UserProfile> {
        val userId = authRepository.getCurrentUserId()
            ?: return ProfileResult.Error("Сессия недействительна. Войдите снова")
        val email = authRepository.getCurrentUserEmail()
        val existing = when (val profile = createOrGetMyProfile()) {
            is ProfileResult.Error -> return profile
            is ProfileResult.Success -> profile.data
        }
        val avatarUrl = "https://example.com/avatars/$userId.png"
        val updated = existing.copy(
            email = email,
            photo = avatarUrl,
        )
        profilesByUserId[userId] = updated
        return ProfileResult.Success(updated)
    }
}
