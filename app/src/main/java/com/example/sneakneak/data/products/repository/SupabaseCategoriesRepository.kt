package com.example.sneakneak.data.products.repository

import com.example.sneakneak.data.core.ProductsErrorMapper
import com.example.sneakneak.data.products.mapper.toDomain
import com.example.sneakneak.data.products.remote.ProductsRemoteDataSource
import com.example.sneakneak.domain.products.model.Category
import com.example.sneakneak.domain.products.model.ProductsResult
import com.example.sneakneak.domain.products.repository.CategoriesRepository

// Отдельный репозиторий категорий для чистой зависимости use case по границам domain.
class SupabaseCategoriesRepository(
    private val remote: ProductsRemoteDataSource,
) : CategoriesRepository {

    override suspend fun getCategories(): ProductsResult<List<Category>> {
        return runCatching {
            remote.getCategories()
                .map { it.toDomain() }
                .sortedBy { it.title.lowercase() }
        }.fold(
            onSuccess = { ProductsResult.Success(it) },
            onFailure = { ProductsResult.Error(ProductsErrorMapper.map(it)) },
        )
    }
}
