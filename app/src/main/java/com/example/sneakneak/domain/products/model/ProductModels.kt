package com.example.sneakneak.domain.products.model

// Domain-модели каталога и акций.
// Product.photo хранит уже готовый URL, который можно напрямую отрисовывать в UI.
data class Product(
    val id: String,
    val title: String,
    val description: String,
    val cost: Double,
    val categoryId: String?,
    val categoryTitle: String?,
    val isBestSeller: Boolean,
    val photo: String?,
)

data class Category(
    val id: String,
    val title: String,
)

data class Promotion(
    val id: String,
    val photo: String?,
)

sealed interface ProductsResult<out T> {
    data class Success<T>(val data: T) : ProductsResult<T>
    data class Error(val message: String) : ProductsResult<Nothing>
}
