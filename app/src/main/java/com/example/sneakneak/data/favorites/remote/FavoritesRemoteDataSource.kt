package com.example.sneakneak.data.favorites.remote

import com.example.sneakneak.data.favorites.dto.FavouriteInsertDto
import com.example.sneakneak.data.favorites.dto.FavouriteRowDto

// Контракт для работы с таблицей `public.favourite`.
interface FavoritesRemoteDataSource {
    suspend fun getByUserId(userId: String): List<FavouriteRowDto>

    suspend fun getByUserIdAndProductId(
        userId: String,
        productId: String,
    ): List<FavouriteRowDto>

    suspend fun insert(favourite: FavouriteInsertDto)

    suspend fun deleteById(id: String)
}
