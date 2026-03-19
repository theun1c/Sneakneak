package com.example.sneakneak.data.core

import java.io.IOException

// Преобразует низкоуровневые ошибки auth (Supabase/network/state) в стабильные UI-сообщения.
object AuthErrorMapper {
    fun map(throwable: Throwable): String = when (classify(throwable)) {
        DataFailure.InvalidCredentials -> "Неверный email или пароль"
        DataFailure.UserNotFound -> "Пользователь не найден"
        DataFailure.AlreadyRegistered -> "Пользователь с таким email уже существует"
        DataFailure.RecoveryNotVerified -> "Сначала подтвердите код восстановления"
        DataFailure.InvalidOtp -> "Неверный код"
        DataFailure.Network -> "Проблема сети. Проверьте подключение к интернету"
        DataFailure.Unauthorized -> "Сессия недействительна. Войдите снова"
        DataFailure.NotConfigured -> "Supabase не настроен. Добавьте SUPABASE_URL и SUPABASE_ANON_KEY"
        is DataFailure.Unknown -> throwable.message ?: "Неизвестная ошибка"
    }

    private fun classify(throwable: Throwable): DataFailure {
        if (throwable is IllegalStateException && throwable.message?.contains("not configured", true) == true) {
            return DataFailure.NotConfigured
        }
        if (throwable is IOException) return DataFailure.Network

        val message = throwable.message?.lowercase().orEmpty()
        return when {
            "invalid login credentials" in message -> DataFailure.InvalidCredentials
            "email not confirmed" in message -> DataFailure.Unauthorized
            "user already registered" in message -> DataFailure.AlreadyRegistered
            "not found" in message -> DataFailure.UserNotFound
            "otp" in message && ("invalid" in message || "expired" in message) -> DataFailure.InvalidOtp
            "recovery" in message && ("not verified" in message || "not found" in message) -> DataFailure.RecoveryNotVerified
            "network" in message || "timeout" in message || "unable to resolve host" in message -> DataFailure.Network
            else -> DataFailure.Unknown(throwable.message)
        }
    }
}
