package com.example.sneakneak.domain.profile.usecase

import com.example.sneakneak.domain.profile.model.ProfileResult
import com.example.sneakneak.domain.profile.model.UserProfile
import com.example.sneakneak.domain.profile.model.UserProfileDraft
import com.example.sneakneak.domain.profile.repository.ProfileRepository
import com.example.sneakneak.domain.profile.validator.ProfilePhoneValidationResult
import com.example.sneakneak.domain.profile.validator.ProfilePhoneValidator

// Use case profile feature: orchestration + бизнес-валидация перед доступом к repository.
class GetMyProfileUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(): ProfileResult<UserProfile> = repository.getMyProfile()
}

class CreateOrGetMyProfileUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(nameHint: String? = null): ProfileResult<UserProfile> {
        return repository.createOrGetMyProfile(nameHint = nameHint)
    }
}

class GetCurrentUserProfileUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(): ProfileResult<UserProfile> = repository.createOrGetMyProfile()
}

class UpsertMyProfileUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(draft: UserProfileDraft): ProfileResult<UserProfile> {
        when (val phoneResult = ProfilePhoneValidator.validate(draft.phone)) {
            is ProfilePhoneValidationResult.Invalid -> return ProfileResult.Error(phoneResult.message)
            ProfilePhoneValidationResult.Valid -> Unit
        }
        return repository.upsertMyProfile(draft)
    }
}

class UpdateMyProfileUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(draft: UserProfileDraft): ProfileResult<UserProfile> {
        // Валидация телефона централизована в domain, а не в UI.
        when (val phoneResult = ProfilePhoneValidator.validate(draft.phone)) {
            is ProfilePhoneValidationResult.Invalid -> return ProfileResult.Error(phoneResult.message)
            ProfilePhoneValidationResult.Valid -> Unit
        }
        return repository.updateMyProfile(draft)
    }
}

class UpdateMyAvatarUseCase(
    private val repository: ProfileRepository,
) {
    suspend operator fun invoke(imageBytes: ByteArray): ProfileResult<UserProfile> {
        return repository.updateMyAvatar(imageBytes)
    }
}

data class ProfileUseCases(
    val getMyProfile: GetMyProfileUseCase,
    val createOrGetMyProfile: CreateOrGetMyProfileUseCase,
    val getCurrentUserProfile: GetCurrentUserProfileUseCase,
    val upsertMyProfile: UpsertMyProfileUseCase,
    val updateMyProfile: UpdateMyProfileUseCase,
    val updateMyAvatar: UpdateMyAvatarUseCase,
)
