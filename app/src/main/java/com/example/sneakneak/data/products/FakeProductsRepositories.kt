package com.example.sneakneak.data.products

import com.example.sneakneak.domain.products.model.Category
import com.example.sneakneak.domain.products.model.Product
import com.example.sneakneak.domain.products.model.ProductsResult
import com.example.sneakneak.domain.products.model.Promotion
import com.example.sneakneak.domain.products.repository.CategoriesRepository
import com.example.sneakneak.domain.products.repository.ProductsRepository

// Fake-репозитории каталога/категорий для dev fallback.
// Сохраняют работоспособность Home/Catalog, когда реальный backend временно недоступен.
class FakeCategoriesRepository : CategoriesRepository {
    override suspend fun getCategories(): ProductsResult<List<Category>> {
        return ProductsResult.Success(
            listOf(
                Category(id = "ea4ed603-8cbe-4d58-a359-b7b843a645bc", title = "Outdoor"),
                Category(id = "4f3a690b-41bf-4fca-8ffc-67cc385c6637", title = "Tennis"),
                Category(id = "76ab9d74-7d5b-4dee-9c67-6ed4019fa202", title = "Men"),
                Category(id = "8143b506-d70a-41ec-a5eb-3cf09627da9e", title = "Women"),
            ),
        )
    }
}

class FakeProductsRepository : ProductsRepository {
    override suspend fun getCatalog(): ProductsResult<List<Product>> {
        return ProductsResult.Success(fakeProducts)
    }

    override suspend fun getBestSellers(): ProductsResult<List<Product>> {
        return ProductsResult.Success(fakeProducts.filter { it.isBestSeller })
    }

    override suspend fun getPromotions(): ProductsResult<List<Promotion>> {
        return ProductsResult.Success(emptyList())
    }

    private companion object {
        val fakeProducts = listOf(
            Product(
                id = "6478da8e-87e6-4a7a-821c-ac38dd861cec",
                title = "PUMA CA Pro Classic",
                description = "Retro running shoes",
                cost = 13999.0,
                categoryId = "ea4ed603-8cbe-4d58-a359-b7b843a645bc",
                categoryTitle = "Outdoor",
                isBestSeller = true,
                photo = null,
            ),
            Product(
                id = "21c2d7c0-a2ea-49a2-9198-52bafafc6958",
                title = "PUMA Velophasis Phased",
                description = "Daily comfort sneakers",
                cost = 16999.0,
                categoryId = "4f3a690b-41bf-4fca-8ffc-67cc385c6637",
                categoryTitle = "Tennis",
                isBestSeller = false,
                photo = null,
            ),
            Product(
                id = "815aa749-975e-4285-bc49-6921556fb9ad",
                title = "Adidas Niteball",
                description = "Cushioned running sneakers",
                cost = 19499.0,
                categoryId = "76ab9d74-7d5b-4dee-9c67-6ed4019fa202",
                categoryTitle = "Men",
                isBestSeller = false,
                photo = null,
            ),
            Product(
                id = "3fb5c391-a841-45fb-9c7a-2f74d11a4bfa",
                title = "Adidas Ozmillen",
                description = "Lightweight fit",
                cost = 20599.0,
                categoryId = "8143b506-d70a-41ec-a5eb-3cf09627da9e",
                categoryTitle = "Women",
                isBestSeller = true,
                photo = null,
            ),
        )
    }
}
