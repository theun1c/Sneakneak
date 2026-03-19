package com.example.sneakneak.data.profile.avatar

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType

// Загрузка аватара в bucket `avatars` по стратегии `<user_id>.png`.
// Для MVP bucket считается public, поэтому после upload возвращается public URL.
class SupabaseAvatarRemoteDataSource(
    private val supabase: SupabaseClient,
) : AvatarRemoteDataSource {
    override suspend fun uploadAvatar(
        userId: String,
        imageBytes: ByteArray,
    ): String {
        require(userId.isNotBlank()) { "userId is required for avatar upload" }
        require(imageBytes.isNotEmpty()) { "Avatar bytes must not be empty" }

        // Единый путь упрощает замену старого аватара и последующую синхронизацию в profile.
        val path = "$userId.png"
        val bucket = supabase.storage.from(AVATARS_BUCKET)
        bucket.upload(path, imageBytes) {
            upsert = true
            contentType = ContentType.Image.PNG
        }
        return bucket.publicUrl(path)
    }

    private companion object {
        const val AVATARS_BUCKET = "avatars"
    }
}
