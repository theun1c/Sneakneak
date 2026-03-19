package com.example.sneakneak.data.products.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// DTO каталожного слоя по schema.sql.
// ProductRowDto включает `photo` как опциональное поле для совместимости с миграциями из contract docs.
@Serializable
data class CategoryRowDto(
    val id: String,
    val title: String,
)

@Serializable
data class ProductRowDto(
    val id: String,
    val title: String,
    @SerialName("category_id")
    val categoryId: String? = null,
    val cost: Double,
    val description: String,
    @SerialName("is_best_seller")
    val isBestSeller: Boolean? = null,
    // `photo` is absent in current schema.sql but can appear after migration from docs/03.
    val photo: String? = null,
)

@Serializable
data class PromotionRowDto(
    val id: String,
    val photo: String? = null,
)
