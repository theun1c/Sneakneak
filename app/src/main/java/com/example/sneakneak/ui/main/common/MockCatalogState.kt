package com.example.sneakneak.ui.main.common

import androidx.compose.runtime.mutableStateListOf

// Temporary in-memory catalog/favorite source for the UI stage.
// TODO(DATA): replace with repositories/use cases so screen state is no longer owned by a UI singleton.
data class MockProduct(
    val id: String,
    val title: String,
    val price: String,
    val category: String = "Outdoor",
    val isBestSeller: Boolean = true,
)

object MockCatalogState {
    val categories = listOf("Все", "Outdoor", "Tennis", "Running")

    val products = listOf(
        MockProduct("air-max-1", "Nike Air Max", "₽752.00", category = "Outdoor"),
        MockProduct("air-max-2", "Nike Club Max", "₽695.00", category = "Running"),
        MockProduct("air-max-3", "Nike Air Max 270", "₽812.00", category = "Tennis"),
        MockProduct("air-max-4", "Nike Air Max Motion", "₽730.00", category = "Outdoor"),
        MockProduct("air-max-5", "Nike Zoom", "₽784.00", category = "Running"),
        MockProduct("air-max-6", "Nike Court Vision", "₽752.00", category = "Tennis"),
    )

    private val favoriteIds = mutableStateListOf<String>()

    fun isFavorite(productId: String): Boolean = favoriteIds.contains(productId)

    fun toggleFavorite(productId: String) {
        if (favoriteIds.contains(productId)) {
            favoriteIds.remove(productId)
        } else {
            favoriteIds.add(productId)
        }
    }

    fun favoriteProducts(): List<MockProduct> = products.filter { favoriteIds.contains(it.id) }

    fun productsFor(category: String): List<MockProduct> {
        if (category == "Все") return products
        return products.filter { it.category == category }
    }

    fun seedFavoritesForPreview() {
        if (favoriteIds.isEmpty()) {
            favoriteIds.addAll(listOf("air-max-1", "air-max-2", "air-max-3"))
        }
    }
}
