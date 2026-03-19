package com.example.sneakneak.data.favorites

import com.example.sneakneak.data.products.FakeProductsRepository
import com.example.sneakneak.domain.favorites.model.FavoritesResult
import com.example.sneakneak.domain.favorites.repository.FavoritesRepository
import com.example.sneakneak.domain.products.model.Product
import com.example.sneakneak.domain.products.model.ProductsResult
import com.example.sneakneak.domain.products.repository.ProductsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Fake-реализация FavoritesRepository.
// Состояние избранного хранится в памяти, но API полностью совпадает с real-реализацией.
class FakeFavoritesRepository(
    private val productsRepository: ProductsRepository = FakeProductsRepository(),
) : FavoritesRepository {
    private val favoriteIds = MutableStateFlow<Set<String>>(emptySet())

    override fun observeFavoriteIds(): StateFlow<Set<String>> = favoriteIds.asStateFlow()

    override suspend fun refreshFavoriteIds(): FavoritesResult<Set<String>> {
        return FavoritesResult.Success(favoriteIds.value)
    }

    override suspend fun getMyFavoriteProducts(): FavoritesResult<List<Product>> {
        val catalog = productsRepository.getCatalog()
        return when (catalog) {
            is ProductsResult.Error -> FavoritesResult.Error(catalog.message)
            is ProductsResult.Success -> {
                FavoritesResult.Success(
                    catalog.data.filter { it.id in favoriteIds.value },
                )
            }
        }
    }

    override suspend fun addFavorite(productId: String): FavoritesResult<Unit> {
        favoriteIds.value = favoriteIds.value + productId
        return FavoritesResult.Success(Unit)
    }

    override suspend fun removeFavorite(productId: String): FavoritesResult<Unit> {
        favoriteIds.value = favoriteIds.value - productId
        return FavoritesResult.Success(Unit)
    }

    override suspend fun toggleFavorite(productId: String): FavoritesResult<Boolean> {
        val isFavoriteNow = if (productId in favoriteIds.value) {
            favoriteIds.value - productId
        } else {
            favoriteIds.value + productId
        }
        favoriteIds.value = isFavoriteNow
        return FavoritesResult.Success(productId in isFavoriteNow)
    }
}
