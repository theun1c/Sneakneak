package com.example.sneakneak.domain.favorites.model

// Результат операций feature избранного.
sealed interface FavoritesResult<out T> {
    data class Success<T>(val data: T) : FavoritesResult<T>
    data class Error(val message: String) : FavoritesResult<Nothing>
}
