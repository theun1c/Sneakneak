package com.example.sneakneak.ui.components

// Универсальная карточка товара для Home/Catalog/Favorite.

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.sneakneak.ui.assets.DesignAssets
import com.example.sneakneak.ui.assets.DesignPngAsset
import com.example.sneakneak.ui.assets.HomeVisualTuning
import com.example.sneakneak.ui.theme.AppColors

// Reusable catalog/home/favorite card.
// Catalog/Home now pass real product data; favorite toggle is still local until Favorite feature is connected.
@Composable
fun ProductCard(
    title: String,
    price: String,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    imageAssetPath: String? = DesignAssets.CardItem,
    isBestSeller: Boolean = true,
    isFavorite: Boolean = false,
    onFavoriteClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AppCircularIconButton(
                    asset = if (isFavorite) AppIconAsset.HeartFilled else AppIconAsset.HeartOutline,
                    onClick = onFavoriteClick,
                    contentDescription = "Избранное",
                    size = 32.dp,
                    containerColor = AppColors.SurfaceVariant,
                    iconTint = if (isFavorite) AppColors.Accent else AppColors.TextSecondary,
                    modifier = Modifier.align(Alignment.TopStart),
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(HomeVisualTuning.productImageContainerSizeDp.dp)
                        .background(AppColors.Surface, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    ProductImage(
                        title = title,
                        imageUrl = imageUrl,
                        fallbackAssetPath = imageAssetPath,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "BEST SELLER",
                style = MaterialTheme.typography.labelMedium,
                color = if (isBestSeller) {
                    AppColors.Primary
                } else {
                    AppColors.Primary.copy(alpha = 0f)
                },
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = AppColors.TextPrimary,
                minLines = HomeVisualTuning.productTitleLineCount,
                maxLines = HomeVisualTuning.productTitleLineCount,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = price,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                    color = AppColors.TextSecondary,
                )

                Box(
                    modifier = Modifier
                        .background(AppColors.Primary, MaterialTheme.shapes.medium)
                        .clickable(onClick = onAddClick)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    AppIcon(
                        asset = AppIconAsset.Plus,
                        contentDescription = "Добавить",
                    )
                }
            }
        }
    }
}

@Composable
private fun ProductImage(
    title: String,
    imageUrl: String?,
    fallbackAssetPath: String?,
) {
    val imageModifier = Modifier
        .size(HomeVisualTuning.productImageSizeDp.dp)
        .offset(x = HomeVisualTuning.productImageOffsetXDp.dp)

    @Composable
    fun fallback() {
        if (fallbackAssetPath != null) {
            DesignPngAsset(
                assetPath = fallbackAssetPath,
                contentDescription = title,
                modifier = imageModifier,
                contentScale = ContentScale.Fit,
            )
        } else {
            AppIcon(
                asset = AppIconAsset.Bag,
                contentDescription = title,
                tint = AppColors.Primary,
                modifier = Modifier.size(48.dp),
            )
        }
    }

    if (imageUrl.isNullOrBlank()) {
        // Если URL не построен или недоступен, показываем placeholder без падения UI.
        fallback()
        return
    }

    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = title,
        modifier = imageModifier,
        contentScale = ContentScale.Fit,
        loading = { fallback() },
        error = { fallback() },
    )
}
