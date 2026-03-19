package com.example.sneakneak.domain.profile.model

// Domain-модели profile feature.
// UserProfile хранит данные, которые приходят из `public.profiles` + email из auth-контекста.
data class UserProfile(
    val userId: String,
    val email: String?,
    val firstname: String,
    val lastname: String,
    val address: String,
    val phone: String,
    val photo: String?,
)

data class UserProfileDraft(
    val firstname: String,
    val lastname: String,
    val address: String,
    val phone: String,
    val photo: String?,
)

sealed interface ProfileResult<out T> {
    data class Success<T>(val data: T) : ProfileResult<T>
    data class Error(val message: String) : ProfileResult<Nothing>
}
