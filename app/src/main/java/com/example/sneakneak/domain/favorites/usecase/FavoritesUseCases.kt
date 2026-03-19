package com.example.sneakneak.domain.favorites.usecase

import com.example.sneakneak.domain.favorites.model.FavoritesResult
import com.example.sneakneak.domain.favorites.repository.FavoritesRepository
import com.example.sneakneak.domain.products.model.Product
import kotlinx.coroutines.flow.StateFlow

// Use case набора избранного.
class ObserveFavoriteIdsUseCase(
    private val repository: FavoritesRepository,
) {
    operator fun invoke(): StateFlow<Set<String>> = repository.observeFavoriteIds()
}

class RefreshFavoriteIdsUseCase(
    private val repository: FavoritesRepository,
) {
    suspend operator fun invoke(): FavoritesResult<Set<String>> = repository.refreshFavoriteIds()
}

class GetMyFavoriteProductsUseCase(
    private val repository: FavoritesRepository,
) {
    suspend operator fun invoke(): FavoritesResult<List<Product>> = repository.getMyFavoriteProducts()
}

class AddFavoriteUseCase(
    private val repository: FavoritesRepository,
) {
    suspend operator fun invoke(productId: String): FavoritesResult<Unit> {
        return repository.addFavorite(productId)
    }
}

class RemoveFavoriteUseCase(
    private val repository: FavoritesRepository,
) {
    suspend operator fun invoke(productId: String): FavoritesResult<Unit> {
        return repository.removeFavorite(productId)
    }
}

class ToggleFavoriteUseCase(
    private val repository: FavoritesRepository,
) {
    suspend operator fun invoke(productId: String): FavoritesResult<Boolean> {
        return repository.toggleFavorite(productId)
    }
}

data class FavoritesUseCases(
    val observeFavoriteIds: ObserveFavoriteIdsUseCase,
    val refreshFavoriteIds: RefreshFavoriteIdsUseCase,
    val getMyFavoriteProducts: GetMyFavoriteProductsUseCase,
    val addFavorite: AddFavoriteUseCase,
    val removeFavorite: RemoveFavoriteUseCase,
    val toggleFavorite: ToggleFavoriteUseCase,
)
