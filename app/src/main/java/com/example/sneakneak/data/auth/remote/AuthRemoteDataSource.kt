package com.example.sneakneak.data.auth.remote

// Контракт data-слоя для низкоуровневых auth-операций.
// Репозиторий работает только с этим интерфейсом и не зависит от конкретной SDK-реализации.
data class RemoteAuthSession(
    val userId: String,
    val email: String?,
)

interface AuthRemoteDataSource {
    suspend fun signUp(email: String, password: String)

    suspend fun signIn(email: String, password: String): RemoteAuthSession

    suspend fun signOut()

    suspend fun sendRecoveryCode(email: String)

    suspend fun verifyRecoveryCode(email: String, code: String)

    suspend fun updatePassword(newPassword: String)

    suspend fun getCurrentSession(): RemoteAuthSession?

    suspend fun getCurrentUserId(): String?

    suspend fun getCurrentUserEmail(): String?
}
