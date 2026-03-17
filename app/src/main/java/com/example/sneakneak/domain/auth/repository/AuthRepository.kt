package com.example.sneakneak.domain.auth.repository

import com.example.sneakneak.domain.auth.model.AuthResult
import com.example.sneakneak.domain.auth.model.AuthSession
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    fun observeSession(): StateFlow<AuthSession?>

    suspend fun signUp(
        name: String,
        email: String,
        password: String,
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

    suspend fun updatePassword(
        email: String,
        newPassword: String,
    ): AuthResult<Unit>

    suspend fun signOut(): AuthResult<Unit>
}
