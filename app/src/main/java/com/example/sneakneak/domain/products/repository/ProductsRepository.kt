package com.example.sneakneak.domain.products.repository

import com.example.sneakneak.domain.products.model.Product
import com.example.sneakneak.domain.products.model.ProductsResult
import com.example.sneakneak.domain.products.model.Promotion

// Domain-контракт product feature (read-only для текущего этапа MVP).
interface ProductsRepository {
    suspend fun getCatalog(): ProductsResult<List<Product>>

    suspend fun getBestSellers(): ProductsResult<List<Product>>

    suspend fun getPromotions(): ProductsResult<List<Promotion>>
}
