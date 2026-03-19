package com.example.sneakneak.data.auth.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

// Реализация AuthRemoteDataSource через Supabase Auth SDK.
// Отвечает только за сетевые вызовы и не содержит UI/domain-правил.
class SupabaseAuthRemoteDataSource(
    private val supabase: SupabaseClient,
) : AuthRemoteDataSource {

    override suspend fun signUp(email: String, password: String) {
        supabase.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    override suspend fun signIn(email: String, password: String): RemoteAuthSession {
        supabase.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
        return requireSession()
    }

    override suspend fun signOut() {
        supabase.auth.signOut()
    }

    override suspend fun sendRecoveryCode(email: String) {
        supabase.auth.resetPasswordForEmail(email)
    }

    override suspend fun verifyRecoveryCode(email: String, code: String) {
        // В recovery flow Supabase ожидает OTP-токен из письма и тип RECOVERY.
        // TODO(SUPABASE): validate against live project settings (email template/recovery mode)
        // when MCP endpoint is available again.
        supabase.auth.verifyEmailOtp(
            type = OtpType.Email.RECOVERY,
            email = email,
            token = code,
        )
    }

    override suspend fun updatePassword(newPassword: String) {
        supabase.auth.updateUser {
            password = newPassword
        }
    }

    override suspend fun getCurrentSession(): RemoteAuthSession? {
        val user = supabase.auth.currentUserOrNull() ?: return null
        return RemoteAuthSession(
            userId = user.id,
            email = user.email,
        )
    }

    override suspend fun getCurrentUserId(): String? = supabase.auth.currentUserOrNull()?.id

    override suspend fun getCurrentUserEmail(): String? = supabase.auth.currentUserOrNull()?.email

    private suspend fun requireSession(): RemoteAuthSession {
        return getCurrentSession()
            ?: throw IllegalStateException("Supabase session was not created")
    }
}
