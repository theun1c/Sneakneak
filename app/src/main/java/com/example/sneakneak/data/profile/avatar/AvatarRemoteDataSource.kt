package com.example.sneakneak.data.profile.avatar

// Контракт data-слоя для загрузки аватара в storage.
// Возвращает конечный URL, который затем сохраняется в `public.profiles.photo`.
interface AvatarRemoteDataSource {
    suspend fun uploadAvatar(
        userId: String,
        imageBytes: ByteArray,
    ): String
}
