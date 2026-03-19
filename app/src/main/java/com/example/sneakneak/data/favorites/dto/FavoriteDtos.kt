package com.example.sneakneak.data.favorites.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// DTO-модели таблицы `favourite` (написание как в schema.sql).
@Serializable
data class FavouriteRowDto(
    val id: String,
    @SerialName("product_id")
    val productId: String? = null,
    @SerialName("user_id")
    val userId: String? = null,
)

@Serializable
data class FavouriteInsertDto(
    @SerialName("product_id")
    val productId: String,
    @SerialName("user_id")
    val userId: String,
)
