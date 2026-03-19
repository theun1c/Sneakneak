package com.example.sneakneak.domain.loyalty.model

import com.example.sneakneak.domain.profile.model.ProfileResult

// Domain-модель loyalty card: данные, достаточные для построения штрихкода и заголовка.
data class LoyaltyCardInfo(
    val userId: String,
    val displayName: String,
)

sealed interface LoyaltyResult<out T> {
    data class Success<T>(val data: T) : LoyaltyResult<T>
    data class Error(val message: String) : LoyaltyResult<Nothing>
}

internal fun ProfileResult.Error.toLoyaltyError(): LoyaltyResult.Error {
    return LoyaltyResult.Error(message)
}
