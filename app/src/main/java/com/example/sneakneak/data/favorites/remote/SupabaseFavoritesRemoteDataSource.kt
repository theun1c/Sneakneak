package com.example.sneakneak.data.favorites.remote

import com.example.sneakneak.data.favorites.dto.FavouriteInsertDto
import com.example.sneakneak.data.favorites.dto.FavouriteRowDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

// Реализация FavoritesRemoteDataSource через PostgREST (`favourite`).
class SupabaseFavoritesRemoteDataSource(
    private val supabase: SupabaseClient,
) : FavoritesRemoteDataSource {

    override suspend fun getByUserId(userId: String): List<FavouriteRowDto> {
        return supabase.from("favourite").select {
            filter {
                eq("user_id", userId)
            }
        }.decodeList<FavouriteRowDto>()
    }

    override suspend fun getByUserIdAndProductId(
        userId: String,
        productId: String,
    ): List<FavouriteRowDto> {
        return supabase.from("favourite").select {
            filter {
                eq("user_id", userId)
                eq("product_id", productId)
            }
        }.decodeList<FavouriteRowDto>()
    }

    override suspend fun insert(favourite: FavouriteInsertDto) {
        supabase.from("favourite").insert(favourite)
    }

    override suspend fun deleteById(id: String) {
        supabase.from("favourite").delete {
            filter {
                eq("id", id)
            }
        }
    }
}
