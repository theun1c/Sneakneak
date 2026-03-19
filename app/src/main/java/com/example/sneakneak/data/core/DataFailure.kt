package com.example.sneakneak.data.core

// Внутренняя классификация ошибок data-слоя.
// Мапперы (Auth/Profile/Products/Favorites) преобразуют эти типы в сообщения для UI.
sealed interface DataFailure {
    data object InvalidCredentials : DataFailure
    data object UserNotFound : DataFailure
    data object AlreadyRegistered : DataFailure
    data object RecoveryNotVerified : DataFailure
    data object InvalidOtp : DataFailure
    data object Network : DataFailure
    data object Unauthorized : DataFailure
    data object NotConfigured : DataFailure
    data class Unknown(val rawMessage: String?) : DataFailure
}
