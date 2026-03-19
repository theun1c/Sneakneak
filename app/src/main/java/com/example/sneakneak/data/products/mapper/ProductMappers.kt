package com.example.sneakneak.data.products.mapper

import com.example.sneakneak.data.products.dto.CategoryRowDto
import com.example.sneakneak.data.products.dto.ProductRowDto
import com.example.sneakneak.data.products.dto.PromotionRowDto
import com.example.sneakneak.data.products.storage.ProductImageUrlResolver
import com.example.sneakneak.domain.products.model.Category
import com.example.sneakneak.domain.products.model.Product
import com.example.sneakneak.domain.products.model.Promotion

// Mapper-ы каталога: DTO -> domain.
// Здесь же подставляется URL изображения через ProductImageUrlResolver.
fun CategoryRowDto.toDomain(): Category {
    return Category(
        id = id,
        title = title,
    )
}

suspend fun ProductRowDto.toDomain(
    categoryTitle: String?,
    imageResolver: ProductImageUrlResolver,
): Product {
    return Product(
        id = id,
        title = title,
        description = description,
        cost = cost,
        categoryId = categoryId,
        categoryTitle = categoryTitle,
        isBestSeller = isBestSeller == true,
        photo = imageResolver.resolveProductPhotoUrl(
            productId = id,
            photo = photo,
        ),
    )
}

fun PromotionRowDto.toDomain(): Promotion {
    return Promotion(
        id = id,
        photo = photo,
    )
}
