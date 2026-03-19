package com.example.sneakneak.di

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

// Слой DI: ленивое создание единого SupabaseClient для всего приложения.
// Клиент кэшируется, чтобы все репозитории использовали одну auth/session/storage сессию.
object SupabaseClientProvider {
    @Volatile
    private var cachedClient: SupabaseClient? = null

    fun getOrCreate(config: SupabaseConfig): SupabaseClient {
        require(config.isConfigured) { "Supabase is not configured" }

        return cachedClient ?: synchronized(this) {
            cachedClient ?: createSupabaseClient(
                supabaseUrl = config.url,
                supabaseKey = config.anonKey,
            ) {
                install(Auth)
                install(Postgrest)
                install(Storage)
            }.also { client ->
                cachedClient = client
            }
        }
    }
}
