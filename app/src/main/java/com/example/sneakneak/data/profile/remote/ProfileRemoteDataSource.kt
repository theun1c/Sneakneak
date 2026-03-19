package com.example.sneakneak.data.profile.remote

import com.example.sneakneak.data.profile.dto.ProfileInsertDto
import com.example.sneakneak.data.profile.dto.ProfileRowDto
import com.example.sneakneak.data.profile.dto.ProfileUpdateDto

// Низкоуровневый контракт работы с таблицей `public.profiles`.
// Репозиторий profile зависит от интерфейса, а не от Supabase SDK напрямую.
interface ProfileRemoteDataSource {
    suspend fun getByUserId(userId: String): ProfileRowDto?

    suspend fun insert(profile: ProfileInsertDto)

    suspend fun updateByUserId(
        userId: String,
        profile: ProfileUpdateDto,
    )
}
