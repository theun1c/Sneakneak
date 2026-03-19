package com.example.sneakneak.data.core

import java.io.IOException

// Маппер ошибок profile feature: нормализует сетевые, auth и RLS-проблемы в понятные сообщения.
object ProfileErrorMapper {
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
                "Недостаточно прав для чтения/обновления профиля"
            "timeout" in message || "unable to resolve host" in message ->
                "Проблема сети. Проверьте подключение к интернету"
            else -> throwable.message ?: "Не удалось выполнить операцию с профилем"
        }
    }
}
