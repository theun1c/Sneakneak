package com.example.sneakneak.ui.theme

// Shape-токены дизайн-системы приложения.

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object AppCorners {
    val small = 12.dp
    val medium = 16.dp
    val large = 24.dp
    val pill = 999.dp
}

val AppShapes = Shapes(
    small = RoundedCornerShape(AppCorners.small),
    medium = RoundedCornerShape(AppCorners.medium),
    large = RoundedCornerShape(AppCorners.large),
)
