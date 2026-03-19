package com.example.sneakneak.domain.auth.repository

import com.example.sneakneak.domain.auth.model.AuthResult
import com.example.sneakneak.domain.auth.model.AuthSession
import kotlinx.coroutines.flow.StateFlow

// Domain-контракт auth.
// Presentation работает только с этим интерфейсом, а конкретная реализация (Supabase/Fake) выбирается в DI.
interface AuthRepository {
    fun observeSession(): StateFlow<AuthSession?>

    suspend fun signUp(
        email: String,
        password: String,
        nameHint: String? = null,
    ): AuthResult<Unit>

    suspend fun signIn(
        email: String,
        password: String,
    ): AuthResult<AuthSession>

    suspend fun sendRecoveryCode(email: String): AuthResult<Unit>

    suspend fun verifyRecoveryCode(
        email: String,
        code: String,
    ): AuthResult<Unit>

    suspend fun updatePassword(newPassword: String): AuthResult<Unit>

    suspend fun signOut(): AuthResult<Unit>

    suspend fun getCurrentUserId(): String?

    suspend fun getCurrentUserEmail(): String?
}
