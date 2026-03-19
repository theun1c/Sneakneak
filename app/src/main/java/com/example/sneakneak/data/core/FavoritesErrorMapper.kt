package com.example.sneakneak.data.core

import java.io.IOException

// Маппер ошибок feature избранного (таблица `favourite` + user session checks).
object FavoritesErrorMapper {
    fun map(throwable: Throwable): String {
        if (throwable is IllegalStateException && throwable.message?.contains("authenticated", true) == true) {
            return "Сессия недействительна. Войдите снова"
        }
        if (throwable is IOException) {
            return "Проблема сети. Проверьте подключение к интернету"
        }

        val message = throwable.message?.lowercase().orEmpty()
        return when {
            "jwt" in message || "unauthorized" in message || "permission denied" in message ->
                "Недостаточно прав для работы с избранным"
            "timeout" in message || "unable to resolve host" in message || "network" in message ->
                "Проблема сети. Проверьте подключение к интернету"
            else -> throwable.message ?: "Не удалось выполнить операцию с избранным"
        }
    }
}
