package com.example.sneakneak.data.products.repository

import com.example.sneakneak.data.core.ProductsErrorMapper
import com.example.sneakneak.data.products.mapper.toDomain
import com.example.sneakneak.data.products.remote.ProductsRemoteDataSource
import com.example.sneakneak.data.products.storage.ProductImageUrlResolver
import com.example.sneakneak.domain.products.model.Product
import com.example.sneakneak.domain.products.model.ProductsResult
import com.example.sneakneak.domain.products.model.Promotion
import com.example.sneakneak.domain.products.repository.ProductsRepository

// Data-репозиторий каталога.
// Собирает продукты с категориями и вычисляет финальный image URL через storage resolver.
class SupabaseProductsRepository(
    private val remote: ProductsRemoteDataSource,
    private val imageResolver: ProductImageUrlResolver,
) : ProductsRepository {

    override suspend fun getCatalog(): ProductsResult<List<Product>> {
        return loadProducts { remote.getProducts() }
    }

    override suspend fun getBestSellers(): ProductsResult<List<Product>> {
        return loadProducts { remote.getBestSellerProducts() }
    }

    override suspend fun getPromotions(): ProductsResult<List<Promotion>> {
        return runCatching {
            remote.getPromotions()
                .map { it.toDomain() }
        }.fold(
            onSuccess = { ProductsResult.Success(it) },
            onFailure = { ProductsResult.Error(ProductsErrorMapper.map(it)) },
        )
    }

    private suspend fun loadProducts(
        block: suspend () -> List<com.example.sneakneak.data.products.dto.ProductRowDto>,
    ): ProductsResult<List<Product>> {
        return runCatching {
            // Категории запрашиваются отдельно: в schema.sql нет join/view под готовую карточку.
            val categoriesById = remote.getCategories()
                .associate { it.id to it.title }
            val rows = block()
            buildList(rows.size) {
                rows.forEach { row ->
                    add(
                        row.toDomain(
                            categoryTitle = categoriesById[row.categoryId],
                            imageResolver = imageResolver,
                        ),
                    )
                }
            }
        }.fold(
            onSuccess = { ProductsResult.Success(it) },
            onFailure = { ProductsResult.Error(ProductsErrorMapper.map(it)) },
        )
    }
}
