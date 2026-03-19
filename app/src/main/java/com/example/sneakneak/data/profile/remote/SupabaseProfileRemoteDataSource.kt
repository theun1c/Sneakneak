package com.example.sneakneak.data.profile.remote

import com.example.sneakneak.data.profile.dto.ProfileInsertDto
import com.example.sneakneak.data.profile.dto.ProfileRowDto
import com.example.sneakneak.data.profile.dto.ProfileUpdateDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

// Реализация ProfileRemoteDataSource через PostgREST.
// Используем `user_id` как главный ключ бизнес-логики, даже если в таблице есть внутренний `id`.
class SupabaseProfileRemoteDataSource(
    private val supabase: SupabaseClient,
) : ProfileRemoteDataSource {

    override suspend fun getByUserId(userId: String): ProfileRowDto? {
        val rows = supabase.from("profiles").select {
            filter {
                eq("user_id", userId)
            }
            limit(1)
        }.decodeList<ProfileRowDto>()
        return rows.firstOrNull()
    }

    override suspend fun insert(profile: ProfileInsertDto) {
        supabase.from("profiles").insert(profile)
    }

    override suspend fun updateByUserId(
        userId: String,
        profile: ProfileUpdateDto,
    ) {
        supabase.from("profiles").update(profile) {
            filter {
                eq("user_id", userId)
            }
        }
    }
}
