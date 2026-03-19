package com.example.sneakneak.data.products.remote

import com.example.sneakneak.data.products.dto.CategoryRowDto
import com.example.sneakneak.data.products.dto.ProductRowDto
import com.example.sneakneak.data.products.dto.PromotionRowDto

// Контракт чтения каталожных сущностей из backend (`categories`, `products`, `actions`).
interface ProductsRemoteDataSource {
    suspend fun getCategories(): List<CategoryRowDto>

    suspend fun getProducts(): List<ProductRowDto>

    suspend fun getBestSellerProducts(): List<ProductRowDto>

    suspend fun getPromotions(): List<PromotionRowDto>
}
