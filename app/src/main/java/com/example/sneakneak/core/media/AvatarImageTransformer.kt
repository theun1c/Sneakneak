package com.example.sneakneak.core.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import java.io.ByteArrayOutputStream
import kotlin.math.max

// Core-утилита для avatar flow: приводит изображение из камеры/галереи к PNG-байтам.
// Используется перед upload в bucket `avatars`, чтобы слой data работал с единым форматом.
object AvatarImageTransformer {
    fun uriToPngBytes(
        context: Context,
        uri: Uri,
        maxSizePx: Int = DEFAULT_MAX_SIZE_PX,
    ): ByteArray? {
        return runCatching {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            val bitmap = ImageDecoder.decodeBitmap(source)
            bitmapToPngBytes(bitmap, maxSizePx)
        }.getOrNull()
    }

    fun bitmapToPngBytes(
        bitmap: Bitmap,
        maxSizePx: Int = DEFAULT_MAX_SIZE_PX,
    ): ByteArray {
        // Ограничение размера снижает payload и уменьшает риск OOM при загрузке аватара.
        val scaled = bitmap.scaledDown(maxSizePx)
        return ByteArrayOutputStream().use { output ->
            scaled.compress(Bitmap.CompressFormat.PNG, 100, output)
            output.toByteArray()
        }.also {
            if (scaled !== bitmap) {
                scaled.recycle()
            }
        }
    }

    private fun Bitmap.scaledDown(maxSizePx: Int): Bitmap {
        if (maxSizePx <= 0) return this
        val maxDimension = max(width, height)
        if (maxDimension <= maxSizePx) return this

        val scale = maxSizePx.toFloat() / maxDimension.toFloat()
        val targetWidth = (width * scale).toInt().coerceAtLeast(1)
        val targetHeight = (height * scale).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(this, targetWidth, targetHeight, true)
    }

    private const val DEFAULT_MAX_SIZE_PX = 1024
}
