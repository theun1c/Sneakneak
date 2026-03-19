package com.example.sneakneak.data.profile.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// DTO-модели таблицы `public.profiles` по текущей schema.sql.
// Разделены на row/insert/update, чтобы явно контролировать формат операций PostgREST.
@Serializable
data class ProfileRowDto(
    val id: String? = null,
    @SerialName("user_id")
    val userId: String,
    val photo: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val address: String? = null,
    val phone: String? = null,
)

@Serializable
data class ProfileInsertDto(
    @SerialName("user_id")
    val userId: String,
    val photo: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val address: String? = null,
    val phone: String? = null,
)

@Serializable
data class ProfileUpdateDto(
    val photo: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val address: String? = null,
    val phone: String? = null,
)
