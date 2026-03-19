package com.example.sneakneak.core.barcode

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter

// Core-утилита: генерация bitmap-штрихкода CODE_128 для экрана Loyalty Card.
// На вход принимает payload (user id), на выходе возвращает готовое изображение для Compose.
class Code128BarcodeGenerator(
    private val writer: MultiFormatWriter = MultiFormatWriter(),
) {
    fun generate(
        payload: String,
        width: Int = DEFAULT_WIDTH,
        height: Int = DEFAULT_HEIGHT,
    ): Bitmap {
        require(payload.isNotBlank()) { "Barcode payload must not be blank" }
        require(width > 0 && height > 0) { "Barcode size must be positive" }

        // Минимальный отступ и UTF-8 нужны для предсказуемого визуала на разных устройствах.
        val hints = mapOf(
            EncodeHintType.MARGIN to 1,
            EncodeHintType.CHARACTER_SET to "UTF-8",
        )
        val matrix = writer.encode(
            payload,
            BarcodeFormat.CODE_128,
            width,
            height,
            hints,
        )

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (matrix[x, y]) BLACK else WHITE)
            }
        }
        return bitmap
    }

    private companion object {
        const val DEFAULT_WIDTH = 1200
        const val DEFAULT_HEIGHT = 420
        const val BLACK = -0x1000000
        const val WHITE = -0x1
    }
}
