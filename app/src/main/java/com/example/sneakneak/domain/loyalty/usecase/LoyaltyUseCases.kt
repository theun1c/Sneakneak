package com.example.sneakneak.domain.loyalty.usecase

import com.example.sneakneak.domain.auth.repository.AuthRepository
import com.example.sneakneak.domain.loyalty.model.LoyaltyCardInfo
import com.example.sneakneak.domain.loyalty.model.LoyaltyResult
import com.example.sneakneak.domain.loyalty.model.toLoyaltyError
import com.example.sneakneak.domain.profile.model.ProfileResult
import com.example.sneakneak.domain.profile.repository.ProfileRepository

// Use case loyalty card.
// Строит карточку на основе auth user id и имени из profile (с fallback на email).
class GetLoyaltyCardInfoUseCase(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(): LoyaltyResult<LoyaltyCardInfo> {
        val userId = authRepository.getCurrentUserId()
            ?: return LoyaltyResult.Error("Сессия недействительна. Войдите снова")
        val fallbackName = authRepository.getCurrentUserEmail()
            ?.substringBefore("@")
            ?.takeIf { it.isNotBlank() }
            ?: "Пользователь"

        return when (val profile = profileRepository.createOrGetMyProfile()) {
            is ProfileResult.Error -> profile.toLoyaltyError()
            is ProfileResult.Success -> {
                val fullName = listOf(profile.data.firstname, profile.data.lastname)
                    .map { it.trim() }
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
                    .ifBlank { fallbackName }
                LoyaltyResult.Success(
                    LoyaltyCardInfo(
                        userId = userId,
                        displayName = fullName,
                    ),
                )
            }
        }
    }
}

data class LoyaltyUseCases(
    val getLoyaltyCardInfo: GetLoyaltyCardInfoUseCase,
)
