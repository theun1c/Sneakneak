package com.example.sneakneak.domain.products.repository

import com.example.sneakneak.domain.products.model.Category
import com.example.sneakneak.domain.products.model.ProductsResult

// Domain-контракт для списка категорий каталога.
interface CategoriesRepository {
    suspend fun getCategories(): ProductsResult<List<Category>>
}
