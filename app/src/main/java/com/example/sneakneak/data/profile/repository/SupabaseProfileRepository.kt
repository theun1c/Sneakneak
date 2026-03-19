package com.example.sneakneak.data.profile.repository

// Слой data/profile: реализация репозитория профиля + интеграция avatar storage.

import com.example.sneakneak.data.core.ProfileErrorMapper
import com.example.sneakneak.data.profile.ProfileBootstrapper
import com.example.sneakneak.data.profile.avatar.AvatarRemoteDataSource
import com.example.sneakneak.data.profile.mapper.toDomain
import com.example.sneakneak.data.profile.mapper.toInsertDto
import com.example.sneakneak.data.profile.mapper.toUpdateDto
import com.example.sneakneak.data.profile.remote.ProfileRemoteDataSource
import com.example.sneakneak.domain.auth.repository.AuthRepository
import com.example.sneakneak.domain.profile.model.ProfileResult
import com.example.sneakneak.domain.profile.model.UserProfile
import com.example.sneakneak.domain.profile.model.UserProfileDraft
import com.example.sneakneak.domain.profile.repository.ProfileRepository

// Data-реализация ProfileRepository.
// Объединяет auth-контекст, таблицу `profiles` и avatar storage в единый поток данных для UI.
class SupabaseProfileRepository(
    private val authRepository: AuthRepository,
    private val remote: ProfileRemoteDataSource,
    private val bootstrapper: ProfileBootstrapper,
    private val avatarRemote: AvatarRemoteDataSource,
) : ProfileRepository {

    override suspend fun getMyProfile(): ProfileResult<UserProfile> {
        return runCatching {
            val userId = requireCurrentUserId()
            val email = authRepository.getCurrentUserEmail()
            // Берем email из auth user, т.к. в schema.sql он не хранится в profiles.
            val profile = remote.getByUserId(userId)
                ?: throw IllegalStateException("Profile was not found")
            ProfileResult.Success(profile.toDomain(email))
        }.getOrElse {
            ProfileResult.Error(ProfileErrorMapper.map(it))
        }
    }

    override suspend fun createOrGetMyProfile(nameHint: String?): ProfileResult<UserProfile> {
        return runCatching {
            val userId = requireCurrentUserId()
            val email = authRepository.getCurrentUserEmail()
            val profile = bootstrapper.createIfMissing(
                userId = userId,
                emailHint = email,
                nameHint = nameHint,
            )
            ProfileResult.Success(profile.toDomain(email))
        }.getOrElse {
            ProfileResult.Error(ProfileErrorMapper.map(it))
        }
    }

    override suspend fun upsertMyProfile(draft: UserProfileDraft): ProfileResult<UserProfile> {
        return runCatching {
            val userId = requireCurrentUserId()
            val email = authRepository.getCurrentUserEmail()
            val existing = remote.getByUserId(userId)
            if (existing == null) {
                remote.insert(draft.toInsertDto(userId))
            } else {
                // В schema.sql нет отдельного RPC upsert, поэтому выполняем insert/update вручную.
                remote.updateByUserId(
                    userId = userId,
                    profile = draft.toUpdateDto(),
                )
            }
            val updated = remote.getByUserId(userId)
                ?: throw IllegalStateException("Profile was not found after save")
            ProfileResult.Success(updated.toDomain(email))
        }.getOrElse {
            ProfileResult.Error(ProfileErrorMapper.map(it))
        }
    }

    override suspend fun updateMyProfile(draft: UserProfileDraft): ProfileResult<UserProfile> {
        return upsertMyProfile(draft)
    }

    override suspend fun updateMyAvatar(imageBytes: ByteArray): ProfileResult<UserProfile> {
        return runCatching {
            val userId = requireCurrentUserId()
            val email = authRepository.getCurrentUserEmail()
            // Гарантируем профиль перед обновлением фото, чтобы update не падал на "row not found".
            val profile = bootstrapper.createIfMissing(
                userId = userId,
                emailHint = email,
                nameHint = null,
            )
            val avatarUrl = avatarRemote.uploadAvatar(userId = userId, imageBytes = imageBytes)
            remote.updateByUserId(
                userId = userId,
                profile = UserProfileDraft(
                    firstname = profile.firstname.orEmpty(),
                    lastname = profile.lastname.orEmpty(),
                    address = profile.address.orEmpty(),
                    phone = profile.phone.orEmpty(),
                    photo = avatarUrl,
                ).toUpdateDto(),
            )
            val updated = remote.getByUserId(userId)
                ?: throw IllegalStateException("Profile was not found after avatar update")
            ProfileResult.Success(updated.toDomain(email))
        }.getOrElse {
            ProfileResult.Error(ProfileErrorMapper.map(it))
        }
    }

    private suspend fun requireCurrentUserId(): String {
        return authRepository.getCurrentUserId()
            ?: throw IllegalStateException("No authenticated user")
    }
}
