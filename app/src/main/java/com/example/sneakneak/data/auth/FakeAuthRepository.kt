package com.example.sneakneak.data.auth

// Слой data/auth: fallback-реализация репозитория без сети.

import com.example.sneakneak.domain.auth.model.AuthResult
import com.example.sneakneak.domain.auth.model.AuthSession
import com.example.sneakneak.domain.auth.model.AuthUser
import com.example.sneakneak.domain.auth.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private data class StoredUser(
    val id: String,
    val name: String,
    val email: String,
    var password: String,
)

// Fake-реализация AuthRepository для offline/dev режима.
// Нужна как безопасный fallback, когда Supabase не настроен или недоступен.
class FakeAuthRepository : AuthRepository {
    private val users = mutableListOf(
        StoredUser(
            id = "mock-user-001",
            name = "Emmanuel Oyiboke",
            email = "demo@sneakneak.com",
            password = "password123",
        ),
    )

    private val activeSession = MutableStateFlow<AuthSession?>(null)
    private val recoveryCodes = mutableMapOf<String, String>()
    private var verifiedRecoveryEmail: String? = null

    override fun observeSession(): StateFlow<AuthSession?> = activeSession.asStateFlow()

    override suspend fun signUp(
        email: String,
        password: String,
        nameHint: String?,
    ): AuthResult<Unit> {
        if (users.any { it.email == email }) {
            return AuthResult.Error("Пользователь с таким email уже существует")
        }
        users += StoredUser(
            id = "mock-user-${users.size + 1}",
            name = nameHint?.trim().takeUnless { it.isNullOrEmpty() } ?: email.substringBefore("@"),
            email = email,
            password = password,
        )
        return AuthResult.Success(Unit)
    }

    override suspend fun signIn(
        email: String,
        password: String,
    ): AuthResult<AuthSession> {
        val user = users.firstOrNull { it.email == email }
            ?: return AuthResult.Error("Пользователь не найден")
        if (user.password != password) {
            return AuthResult.Error("Неверный пароль")
        }

        val session = AuthSession(
            user = AuthUser(
                id = user.id,
                name = user.name,
                email = user.email,
            ),
        )
        activeSession.value = session
        return AuthResult.Success(session)
    }

    override suspend fun sendRecoveryCode(email: String): AuthResult<Unit> {
        if (users.none { it.email == email }) {
            return AuthResult.Error("Пользователь с таким email не найден")
        }
        // TODO(DATA): replace fixed OTP with backend-generated recovery token.
        recoveryCodes[email] = DEFAULT_RECOVERY_CODE
        verifiedRecoveryEmail = null
        return AuthResult.Success(Unit)
    }

    override suspend fun verifyRecoveryCode(
        email: String,
        code: String,
    ): AuthResult<Unit> {
        val expectedCode = recoveryCodes[email]
            ?: return AuthResult.Error("Сначала запросите код восстановления")
        if (expectedCode != code) {
            return AuthResult.Error("Неверный код")
        }
        verifiedRecoveryEmail = email
        return AuthResult.Success(Unit)
    }

    override suspend fun updatePassword(newPassword: String): AuthResult<Unit> {
        val email = verifiedRecoveryEmail
        if (email == null) {
            return AuthResult.Error("Сначала подтвердите код восстановления")
        }
        val index = users.indexOfFirst { it.email == email }
        if (index == -1) {
            return AuthResult.Error("Пользователь с таким email не найден")
        }
        users[index] = users[index].copy(password = newPassword)
        verifiedRecoveryEmail = null
        recoveryCodes.remove(email)
        activeSession.value = null
        return AuthResult.Success(Unit)
    }

    override suspend fun signOut(): AuthResult<Unit> {
        activeSession.value = null
        return AuthResult.Success(Unit)
    }

    override suspend fun getCurrentUserId(): String? = activeSession.value?.user?.id

    override suspend fun getCurrentUserEmail(): String? = activeSession.value?.user?.email

    companion object {
        // Shared by UI tests and mock manual QA scenarios.
        const val DEFAULT_RECOVERY_CODE = "123456"
    }
}
