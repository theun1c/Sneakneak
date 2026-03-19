package com.example.sneakneak.domain.favorites.repository

import com.example.sneakneak.domain.favorites.model.FavoritesResult
import com.example.sneakneak.domain.products.model.Product
import kotlinx.coroutines.flow.StateFlow

// Domain-контракт feature избранного.
// observeFavoriteIds служит единым live-источником для синхронизации иконок на разных экранах.
interface FavoritesRepository {
    fun observeFavoriteIds(): StateFlow<Set<String>>

    suspend fun refreshFavoriteIds(): FavoritesResult<Set<String>>

    suspend fun getMyFavoriteProducts(): FavoritesResult<List<Product>>

    suspend fun addFavorite(productId: String): FavoritesResult<Unit>

    suspend fun removeFavorite(productId: String): FavoritesResult<Unit>

    suspend fun toggleFavorite(productId: String): FavoritesResult<Boolean>
}
