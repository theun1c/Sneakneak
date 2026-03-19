package com.example.sneakneak.ui.components

// Компонент аватара с fallback на инициалы.
// При наличии URL загружает фото через Coil.

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.sneakneak.ui.theme.AppColors

@Composable
fun ProfileAvatar(
    name: String,
    modifier: Modifier = Modifier,
    imageUrl: String? = null,
    imagePainter: Painter? = null,
    size: Dp = 96.dp,
) {
    @Composable
    fun placeholder() {
        Text(
            text = name.trim().take(2).uppercase(),
            style = MaterialTheme.typography.titleLarge,
            color = AppColors.PrimaryDark,
            textAlign = TextAlign.Center,
        )
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(AppColors.SurfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        when {
            !imageUrl.isNullOrBlank() -> {
                SubcomposeAsyncImage(
                    model = imageUrl,
                    contentDescription = name,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop,
                    loading = { placeholder() },
                    error = { placeholder() },
                )
            }

            imagePainter != null -> {
                Image(
                    painter = imagePainter,
                    contentDescription = name,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            else -> placeholder()
        }
    }
}
