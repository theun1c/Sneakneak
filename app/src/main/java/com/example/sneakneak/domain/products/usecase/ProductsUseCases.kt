package com.example.sneakneak.domain.products.usecase

import com.example.sneakneak.domain.products.model.Category
import com.example.sneakneak.domain.products.model.Product
import com.example.sneakneak.domain.products.model.ProductsResult
import com.example.sneakneak.domain.products.model.Promotion
import com.example.sneakneak.domain.products.repository.CategoriesRepository
import com.example.sneakneak.domain.products.repository.ProductsRepository

// Use case каталога/Home: тонкая прослойка между presentation и repository.
class GetCatalogProductsUseCase(
    private val repository: ProductsRepository,
) {
    suspend operator fun invoke(): ProductsResult<List<Product>> = repository.getCatalog()
}

class GetBestSellerProductsUseCase(
    private val repository: ProductsRepository,
) {
    suspend operator fun invoke(): ProductsResult<List<Product>> = repository.getBestSellers()
}

class GetPromotionsUseCase(
    private val repository: ProductsRepository,
) {
    suspend operator fun invoke(): ProductsResult<List<Promotion>> = repository.getPromotions()
}

class GetCategoriesUseCase(
    private val repository: CategoriesRepository,
) {
    suspend operator fun invoke(): ProductsResult<List<Category>> = repository.getCategories()
}

data class ProductsUseCases(
    val getCatalogProducts: GetCatalogProductsUseCase,
    val getBestSellerProducts: GetBestSellerProductsUseCase,
    val getPromotions: GetPromotionsUseCase,
    val getCategories: GetCategoriesUseCase,
)
