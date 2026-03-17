package com.example.sneakneak.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.sneakneak.R
import com.example.sneakneak.ui.theme.AppColors

enum class AppIconAsset(@DrawableRes val resId: Int) {
    ArrowLeft(R.drawable.ic_arrow_left),
    Search(R.drawable.ic_search),
    Filter(R.drawable.ic_filter),
    Eye(R.drawable.ic_eye),
    EyeOff(R.drawable.ic_eye_off),
    HeartOutline(R.drawable.ic_heart_outline),
    HeartFilled(R.drawable.ic_heart_filled),
    Plus(R.drawable.ic_plus),
    Home(R.drawable.ic_home),
    Bag(R.drawable.ic_bag),
    Profile(R.drawable.ic_profile),
    Edit(R.drawable.ic_edit),
    Mail(R.drawable.ic_mail),
    Menu(R.drawable.ic_menu),
    Bell(R.drawable.ic_bell),
    ChevronRight(R.drawable.ic_chevron_right),
    Settings(R.drawable.ic_settings),
    Truck(R.drawable.ic_truck),
    Logout(R.drawable.ic_logout),
}

@Composable
fun AppIcon(
    asset: AppIconAsset,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color? = null,
) {
    Image(
        painter = painterResource(id = asset.resId),
        contentDescription = contentDescription,
        modifier = modifier,
        colorFilter = tint?.let(ColorFilter::tint),
    )
}

@Composable
fun AppCircularIconButton(
    asset: AppIconAsset,
    onClick: () -> Unit,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    iconTint: Color = AppColors.TextPrimary,
) {
    Surface(
        modifier = modifier
            .size(size)
            .clickable(role = Role.Button, onClick = onClick),
        shape = CircleShape,
        color = containerColor,
        shadowElevation = 0.dp,
    ) {
        Box(contentAlignment = Alignment.Center) {
            AppIcon(
                asset = asset,
                contentDescription = contentDescription,
                modifier = Modifier.size(20.dp),
                tint = iconTint,
            )
        }
    }
}
