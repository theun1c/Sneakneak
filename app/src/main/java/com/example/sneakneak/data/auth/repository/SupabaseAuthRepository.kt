package com.example.sneakneak.data.auth.repository

// Слой data/auth: реальная реализация AuthRepository через Supabase.

import com.example.sneakneak.data.auth.remote.AuthRemoteDataSource
import com.example.sneakneak.data.auth.remote.RemoteAuthSession
import com.example.sneakneak.data.core.AuthErrorMapper
import com.example.sneakneak.data.profile.ProfileBootstrapper
import com.example.sneakneak.domain.auth.model.AuthResult
import com.example.sneakneak.domain.auth.model.AuthSession
import com.example.sneakneak.domain.auth.model.AuthUser
import com.example.sneakneak.domain.auth.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking

// Data-реализация AuthRepository поверх Supabase.
// Здесь собирается полный auth-поток: sign up/sign in/recovery/session restore + bootstrap profile.
class SupabaseAuthRepository(
    private val remote: AuthRemoteDataSource,
    private val profileBootstrapper: ProfileBootstrapper? = null,
) : AuthRepository {
    private val activeSession = MutableStateFlow<AuthSession?>(null)
    private var verifiedRecoveryEmail: String? = null
    private val pendingNameByEmail = mutableMapOf<String, String>()

    init {
        runCatching {
            runBlocking(Dispatchers.IO) {
                restoreSession()
            }
        }.onFailure {
            // TODO(SUPABASE): add structured logger once app-wide logging abstraction is introduced.
            activeSession.value = null
        }
    }

    override fun observeSession(): StateFlow<AuthSession?> = activeSession.asStateFlow()

    override suspend fun signUp(
        email: String,
        password: String,
        nameHint: String?,
    ): AuthResult<Unit> {
        return runCatching {
            remote.signUp(email = email, password = password)
            val normalizedEmail = email.trim().lowercase()
            val normalizedNameHint = nameHint?.trim().takeUnless { it.isNullOrEmpty() }

            // Contract from docs: after successful sign-up user should return to Sign In.
            // If backend auto-opens a session, close it immediately.
            val autoSession = remote.getCurrentSession()
            if (autoSession != null) {
                val bootstrapResult = runCatching {
                    bootstrapProfileIfNeeded(
                        userId = autoSession.userId,
                        email = autoSession.email,
                        nameHint = normalizedNameHint,
                    )
                }
                remote.signOut()
                if (bootstrapResult.isFailure && normalizedNameHint != null) {
                    pendingNameByEmail[normalizedEmail] = normalizedNameHint
                } else {
                    pendingNameByEmail.remove(normalizedEmail)
                }
            } else if (normalizedNameHint != null) {
                pendingNameByEmail[normalizedEmail] = normalizedNameHint
            }
            activeSession.value = null
            AuthResult.Success(Unit)
        }.getOrElse {
            AuthResult.Error(AuthErrorMapper.map(it))
        }
    }

    override suspend fun signIn(email: String, password: String): AuthResult<AuthSession> {
        return runCatching {
            val session = remote.signIn(email = email, password = password).toDomain()
            val normalizedEmail = email.trim().lowercase()
            val pendingName = pendingNameByEmail[normalizedEmail]
            // Подстраховка для случая, когда профиль не был создан во время регистрации.
            val bootstrapResult = runCatching {
                bootstrapProfileIfNeeded(
                    userId = session.user.id,
                    email = session.user.email,
                    nameHint = pendingName,
                )
            }
            if (bootstrapResult.isSuccess) {
                pendingNameByEmail.remove(normalizedEmail)
            }
            activeSession.value = session
            session
        }.fold(
            onSuccess = { AuthResult.Success(it) },
            onFailure = { AuthResult.Error(AuthErrorMapper.map(it)) },
        )
    }

    override suspend fun sendRecoveryCode(email: String): AuthResult<Unit> {
        return runCatching {
            remote.sendRecoveryCode(email)
            verifiedRecoveryEmail = null
            AuthResult.Success(Unit)
        }.getOrElse {
            AuthResult.Error(AuthErrorMapper.map(it))
        }
    }

    override suspend fun verifyRecoveryCode(email: String, code: String): AuthResult<Unit> {
        return runCatching {
            remote.verifyRecoveryCode(email = email, code = code)
            verifiedRecoveryEmail = email
            AuthResult.Success(Unit)
        }.getOrElse {
            AuthResult.Error(AuthErrorMapper.map(it))
        }
    }

    override suspend fun updatePassword(newPassword: String): AuthResult<Unit> {
        return runCatching {
            if (verifiedRecoveryEmail == null) {
                throw IllegalStateException("Recovery not verified")
            }
            remote.updatePassword(newPassword)
            verifiedRecoveryEmail = null
            remote.signOut()
            activeSession.value = null
            AuthResult.Success(Unit)
        }.getOrElse {
            AuthResult.Error(AuthErrorMapper.map(it))
        }
    }

    override suspend fun signOut(): AuthResult<Unit> {
        return runCatching {
            remote.signOut()
            activeSession.value = null
            AuthResult.Success(Unit)
        }.getOrElse {
            AuthResult.Error(AuthErrorMapper.map(it))
        }
    }

    override suspend fun getCurrentUserId(): String? {
        return remote.getCurrentUserId() ?: activeSession.value?.user?.id
    }

    override suspend fun getCurrentUserEmail(): String? {
        return remote.getCurrentUserEmail() ?: activeSession.value?.user?.email
    }

    private suspend fun restoreSession() {
        // Восстановление сессии при старте приложения: Splash читает это состояние.
        activeSession.value = remote.getCurrentSession()?.toDomain()
    }

    private suspend fun bootstrapProfileIfNeeded(
        userId: String,
        email: String?,
        nameHint: String?,
    ) {
        val bootstrapper = profileBootstrapper ?: return
        bootstrapper.createIfMissing(
            userId = userId,
            emailHint = email,
            nameHint = nameHint,
        )
    }

    private fun RemoteAuthSession.toDomain(): AuthSession {
        return AuthSession(
            user = AuthUser(
                id = userId,
                name = email?.substringBefore("@").orEmpty().ifBlank { "User" },
                email = email.orEmpty(),
            ),
        )
    }
}
