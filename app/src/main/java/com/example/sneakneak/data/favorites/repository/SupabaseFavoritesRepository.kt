package com.example.sneakneak.data.favorites.repository

// Слой data/favorites: синхронизация избранного пользователя с таблицей `favourite`.

import com.example.sneakneak.data.core.FavoritesErrorMapper
import com.example.sneakneak.data.favorites.dto.FavouriteInsertDto
import com.example.sneakneak.data.favorites.remote.FavoritesRemoteDataSource
import com.example.sneakneak.data.products.mapper.toDomain
import com.example.sneakneak.data.products.remote.ProductsRemoteDataSource
import com.example.sneakneak.data.products.storage.ProductImageUrlResolver
import com.example.sneakneak.domain.auth.repository.AuthRepository
import com.example.sneakneak.domain.favorites.model.FavoritesResult
import com.example.sneakneak.domain.favorites.repository.FavoritesRepository
import com.example.sneakneak.domain.products.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Data-репозиторий избранного.
// Держит единый поток favoriteIds, который подписывают Home/Catalog/Favorite экраны.
class SupabaseFavoritesRepository(
    private val authRepository: AuthRepository,
    private val remote: FavoritesRemoteDataSource,
    private val productsRemote: ProductsRemoteDataSource,
    private val imageResolver: ProductImageUrlResolver,
) : FavoritesRepository {
    private val favoriteIds = MutableStateFlow<Set<String>>(emptySet())

    override fun observeFavoriteIds(): StateFlow<Set<String>> = favoriteIds.asStateFlow()

    override suspend fun refreshFavoriteIds(): FavoritesResult<Set<String>> {
        return runCatching {
            val userId = requireCurrentUserId()
            refreshFavoriteIdsInternal(userId)
        }.fold(
            onSuccess = { FavoritesResult.Success(it) },
            onFailure = { FavoritesResult.Error(FavoritesErrorMapper.map(it)) },
        )
    }

    override suspend fun getMyFavoriteProducts(): FavoritesResult<List<Product>> {
        return runCatching {
            val userId = requireCurrentUserId()
            val productIds = refreshFavoriteIdsInternal(userId)
            if (productIds.isEmpty()) return@runCatching emptyList()

            val categoriesById = productsRemote.getCategories()
                .associate { it.id to it.title }
            val products = productsRemote.getProducts()
                .filter { it.id in productIds }
            buildList(products.size) {
                products.forEach { row ->
                    add(
                        row.toDomain(
                            categoryTitle = categoriesById[row.categoryId],
                            imageResolver = imageResolver,
                        ),
                    )
                }
            }
        }.fold(
            onSuccess = { FavoritesResult.Success(it) },
            onFailure = { FavoritesResult.Error(FavoritesErrorMapper.map(it)) },
        )
    }

    override suspend fun addFavorite(productId: String): FavoritesResult<Unit> {
        return runCatching {
            val normalizedProductId = productId.trim()
            val userId = requireCurrentUserId()
            val existingRows = remote.getByUserIdAndProductId(userId, normalizedProductId)
            if (existingRows.isEmpty()) {
                remote.insert(
                    FavouriteInsertDto(
                        userId = userId,
                        productId = normalizedProductId,
                    ),
                )
            } else if (existingRows.size > 1) {
                // schema.sql may miss unique(user_id, product_id), so we normalize duplicates in data layer.
                existingRows.drop(1).forEach { duplicate ->
                    remote.deleteById(duplicate.id)
                }
            }
            refreshFavoriteIdsInternal(userId)
            Unit
        }.fold(
            onSuccess = { FavoritesResult.Success(Unit) },
            onFailure = { FavoritesResult.Error(FavoritesErrorMapper.map(it)) },
        )
    }

    override suspend fun removeFavorite(productId: String): FavoritesResult<Unit> {
        return runCatching {
            val normalizedProductId = productId.trim()
            val userId = requireCurrentUserId()
            val rows = remote.getByUserIdAndProductId(userId, normalizedProductId)
            rows.forEach { row ->
                remote.deleteById(row.id)
            }
            refreshFavoriteIdsInternal(userId)
            Unit
        }.fold(
            onSuccess = { FavoritesResult.Success(Unit) },
            onFailure = { FavoritesResult.Error(FavoritesErrorMapper.map(it)) },
        )
    }

    override suspend fun toggleFavorite(productId: String): FavoritesResult<Boolean> {
        return runCatching {
            val normalizedProductId = productId.trim()
            val userId = requireCurrentUserId()
            val rows = remote.getByUserIdAndProductId(userId, normalizedProductId)
            val isFavoriteNow = if (rows.isEmpty()) {
                remote.insert(
                    FavouriteInsertDto(
                        userId = userId,
                        productId = normalizedProductId,
                    ),
                )
                true
            } else {
                rows.forEach { row ->
                    remote.deleteById(row.id)
                }
                false
            }
            refreshFavoriteIdsInternal(userId)
            isFavoriteNow
        }.fold(
            onSuccess = { FavoritesResult.Success(it) },
            onFailure = { FavoritesResult.Error(FavoritesErrorMapper.map(it)) },
        )
    }

    private suspend fun refreshFavoriteIdsInternal(userId: String): Set<String> {
        // Источник истины для иконок сердечка в карточках.
        val ids = remote.getByUserId(userId)
            .mapNotNull { it.productId?.trim() }
            .filter { it.isNotBlank() }
            .toSet()
        favoriteIds.value = ids
        return ids
    }

    private suspend fun requireCurrentUserId(): String {
        return authRepository.getCurrentUserId()
            ?: throw IllegalStateException("No authenticated user")
    }
}
