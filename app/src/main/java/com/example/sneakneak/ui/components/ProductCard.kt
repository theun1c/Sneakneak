package com.example.sneakneak.ui.components

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
import com.example.sneakneak.ui.assets.DesignAssets
import com.example.sneakneak.ui.assets.DesignPngAsset
import com.example.sneakneak.ui.assets.HomeVisualTuning
import com.example.sneakneak.ui.theme.AppColors

// Reusable catalog/home/favorite card.
// The card is UI-first for now; product actions still resolve against mock state until
// product/favorite repositories replace the temporary source of truth.
@Composable
fun ProductCard(
    title: String,
    price: String,
    modifier: Modifier = Modifier,
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
                    if (imageAssetPath != null) {
                        DesignPngAsset(
                            assetPath = imageAssetPath,
                            contentDescription = title,
                            modifier = Modifier
                                .size(HomeVisualTuning.productImageSizeDp.dp)
                                .offset(x = HomeVisualTuning.productImageOffsetXDp.dp),
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
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (isBestSeller) {
                Text(
                    text = "BEST SELLER",
                    style = MaterialTheme.typography.labelMedium,
                    color = AppColors.Primary,
                )
            }

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
