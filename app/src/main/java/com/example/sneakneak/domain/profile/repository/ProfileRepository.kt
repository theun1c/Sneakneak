package com.example.sneakneak.domain.profile.repository

import com.example.sneakneak.domain.profile.model.ProfileResult
import com.example.sneakneak.domain.profile.model.UserProfile
import com.example.sneakneak.domain.profile.model.UserProfileDraft

// Domain-контракт profile feature.
// Изолирует presentation от деталей работы с таблицей `profiles` и avatar storage.
interface ProfileRepository {
    suspend fun getMyProfile(): ProfileResult<UserProfile>

    suspend fun createOrGetMyProfile(nameHint: String? = null): ProfileResult<UserProfile>

    suspend fun upsertMyProfile(draft: UserProfileDraft): ProfileResult<UserProfile>

    suspend fun updateMyProfile(draft: UserProfileDraft): ProfileResult<UserProfile>

    suspend fun updateMyAvatar(imageBytes: ByteArray): ProfileResult<UserProfile>
}
