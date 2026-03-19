package com.example.sneakneak.ui.main.common

// Mapper domain -> UI для карточек товаров и списков каталога.

import com.example.sneakneak.domain.products.model.Product
import java.text.NumberFormat
import java.util.Locale

fun Product.toCatalogProductUiModel(
    isFavorite: Boolean,
): CatalogProductUiModel {
    return CatalogProductUiModel(
        id = id,
        title = title,
        price = formatPrice(cost),
        imageUrl = photo,
        isBestSeller = isBestSeller,
        categoryId = categoryId,
        isFavorite = isFavorite,
    )
}

private fun formatPrice(cost: Double): String {
    // Форматируем цену под UX макет (`ru-RU`) и добавляем символ рубля.
    val formatter = NumberFormat.getIntegerInstance(Locale("ru", "RU"))
    return "${formatter.format(cost)} ₽"
}
