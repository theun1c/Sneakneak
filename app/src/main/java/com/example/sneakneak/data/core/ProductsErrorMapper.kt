package com.example.sneakneak.data.core

import java.io.IOException

// Маппер ошибок чтения каталога/категорий/акций из Supabase.
object ProductsErrorMapper {
    fun map(throwable: Throwable): String {
        if (throwable is IllegalStateException && throwable.message?.contains("not configured", true) == true) {
            return "Supabase не настроен. Добавьте SUPABASE_URL и SUPABASE_ANON_KEY"
        }
        if (throwable is IOException) {
            return "Проблема сети. Проверьте подключение к интернету"
        }

        val message = throwable.message?.lowercase().orEmpty()
        return when {
            "jwt" in message || "unauthorized" in message || "permission denied" in message ->
                "Недостаточно прав для чтения каталога"
            "timeout" in message || "unable to resolve host" in message || "network" in message ->
                "Проблема сети. Проверьте подключение к интернету"
            else -> throwable.message ?: "Не удалось загрузить данные каталога"
        }
    }
}
