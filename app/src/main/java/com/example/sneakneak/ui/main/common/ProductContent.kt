package com.example.sneakneak.ui.main.common

// Переиспользуемые блоки отображения каталожных карточек: секции, grid и empty-state.

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sneakneak.ui.components.ProductCard
import com.example.sneakneak.ui.theme.AppColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

@Composable
fun ProductPairRow(
    products: List<CatalogProductUiModel>,
    onFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        products.forEach { product ->
            ProductCard(
                title = product.title,
                price = product.price,
                imageUrl = product.imageUrl,
                modifier = Modifier.weight(1f),
                isBestSeller = product.isBestSeller,
                isFavorite = product.isFavorite,
                onFavoriteClick = { onFavoriteClick(product.id) },
            )
        }
    }
}

@Composable
fun ProductSectionHeader(
    title: String,
    action: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = AppColors.TextPrimary,
        )
        if (action != null) {
            Text(
                text = action,
                modifier = if (onActionClick != null) {
                    Modifier.clickable(onClick = onActionClick)
                } else {
                    Modifier
                },
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = AppColors.Primary,
            )
        }
    }
}

@Composable
fun ProductsGrid(
    products: List<CatalogProductUiModel>,
    onFavoriteClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        items(products, key = { it.id }) { product ->
            ProductCard(
                title = product.title,
                price = product.price,
                imageUrl = product.imageUrl,
                isBestSeller = product.isBestSeller,
                isFavorite = product.isFavorite,
                onFavoriteClick = { onFavoriteClick(product.id) },
            )
        }
    }
}

@Composable
fun FavoriteEmptyState(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Избранное пока пусто",
            style = MaterialTheme.typography.headlineSmall,
            color = AppColors.TextPrimary,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Нажмите на сердечко у товара, и он появится здесь.",
            style = MaterialTheme.typography.bodyLarge,
            color = AppColors.TextSecondary,
        )
    }
}
