package com.example.sneakneak.ui.components

// Нижняя навигация main-части приложения.
// Каталог рендерится отдельной центральной кнопкой, остальные маршруты идут обычными иконками.

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sneakneak.ui.navigation.AppRoutes
import com.example.sneakneak.ui.theme.AppColors

data class BottomNavItem(
    val route: String,
    val icon: AppIconAsset,
    val contentDescription: String,
)

val DefaultBottomNavItems = listOf(
    BottomNavItem(AppRoutes.Home.route, AppIconAsset.Home, "Главная"),
    BottomNavItem(AppRoutes.Favorite.route, AppIconAsset.HeartOutline, "Избранное"),
    BottomNavItem(AppRoutes.Catalog.route, AppIconAsset.Bag, "Каталог"),
    BottomNavItem(AppRoutes.Notifications.route, AppIconAsset.Bell, "Уведомления"),
    BottomNavItem(AppRoutes.Profile.route, AppIconAsset.Profile, "Профиль"),
)

@Composable
fun BottomNavBar(
    currentRoute: String,
    onItemClick: (BottomNavItem) -> Unit,
    modifier: Modifier = Modifier,
    items: List<BottomNavItem> = DefaultBottomNavItems,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(top = 20.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = AppColors.Surface,
            shadowElevation = 12.dp,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(76.dp)
                    .padding(horizontal = 24.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                items.forEach { item ->
                    if (item.route == AppRoutes.Catalog.route) {
                        SpacerSlot()
                    } else {
                        val selected = currentRoute == item.route
                        AppCircularIconButton(
                            asset = item.icon,
                            onClick = { onItemClick(item) },
                            contentDescription = item.contentDescription,
                            size = 40.dp,
                            containerColor = AppColors.Surface,
                            iconTint = if (selected) AppColors.Primary else AppColors.TextSecondary,
                        )
                    }
                }
            }
        }
        val centerItem = items.firstOrNull { it.route == AppRoutes.Catalog.route }
        if (centerItem != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(64.dp)
                    .background(AppColors.Primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                AppCircularIconButton(
                    asset = centerItem.icon,
                    onClick = { onItemClick(centerItem) },
                    contentDescription = centerItem.contentDescription,
                    size = 64.dp,
                    containerColor = AppColors.Primary,
                    iconTint = AppColors.OnPrimary,
                )
            }
        }
    }
}

@Composable
private fun SpacerSlot() {
    Box(modifier = Modifier.size(40.dp))
}
