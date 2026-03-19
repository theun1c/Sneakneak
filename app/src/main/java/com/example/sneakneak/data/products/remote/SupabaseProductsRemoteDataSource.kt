package com.example.sneakneak.data.products.remote

import com.example.sneakneak.data.products.dto.CategoryRowDto
import com.example.sneakneak.data.products.dto.ProductRowDto
import com.example.sneakneak.data.products.dto.PromotionRowDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

// Реализация ProductsRemoteDataSource через PostgREST.
// Таблицы соответствуют schema.sql: `categories`, `products`, `actions`.
class SupabaseProductsRemoteDataSource(
    private val supabase: SupabaseClient,
) : ProductsRemoteDataSource {

    override suspend fun getCategories(): List<CategoryRowDto> {
        return supabase.from("categories")
            .select {}
            .decodeList<CategoryRowDto>()
    }

    override suspend fun getProducts(): List<ProductRowDto> {
        return supabase.from("products")
            .select {}
            .decodeList<ProductRowDto>()
    }

    override suspend fun getBestSellerProducts(): List<ProductRowDto> {
        return supabase.from("products")
            .select {
                filter {
                    eq("is_best_seller", true)
                }
            }
            .decodeList<ProductRowDto>()
    }

    override suspend fun getPromotions(): List<PromotionRowDto> {
        return supabase.from("actions")
            .select {}
            .decodeList<PromotionRowDto>()
    }
}
