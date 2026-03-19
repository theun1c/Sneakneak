package com.example.sneakneak.di

import com.example.sneakneak.BuildConfig

// Слой DI: объект конфигурации Supabase.
// Значения приходят из BuildConfig, а сами секреты задаются через local.properties/gradle.properties.
data class SupabaseConfig(
    val url: String,
    val anonKey: String,
) {
    val isConfigured: Boolean
        get() = url.isNotBlank() && anonKey.isNotBlank()
}

// Централизованная точка чтения SUPABASE_URL и SUPABASE_ANON_KEY.
object SupabaseConfigProvider {
    fun fromBuildConfig(): SupabaseConfig {
        return SupabaseConfig(
            url = BuildConfig.SUPABASE_URL.trim(),
            anonKey = BuildConfig.SUPABASE_ANON_KEY.trim(),
        )
    }
}
