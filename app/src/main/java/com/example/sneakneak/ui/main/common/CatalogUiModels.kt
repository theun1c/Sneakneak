package com.example.sneakneak.ui.main.common

// UI-модели main/catalog/home слоёв.
// Отделены от domain-моделей, чтобы presentation мог безопасно адаптировать формат данных.
data class CatalogProductUiModel(
    val id: String,
    val title: String,
    val price: String,
    val imageUrl: String?,
    val isBestSeller: Boolean,
    val categoryId: String?,
    val isFavorite: Boolean = false,
)

data class HomePromotionUiModel(
    val id: String,
    val imageUrl: String?,
)
