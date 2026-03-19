package com.example.sneakneak.domain.auth.model

// Domain-модели auth feature.
// Они не зависят от Supabase SDK и используются во всех слоях выше data.
data class AuthUser(
    val id: String,
    val name: String,
    val email: String,
)

data class AuthSession(
    val user: AuthUser,
)

sealed interface AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>
    data class Error(val message: String) : AuthResult<Nothing>
}

sealed interface ValidationResult {
    data object Valid : ValidationResult
    data class Invalid(val message: String) : ValidationResult
}
