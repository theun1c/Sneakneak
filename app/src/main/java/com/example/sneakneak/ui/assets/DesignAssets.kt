package com.example.sneakneak.ui.assets

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

// Asset registry for locally bundled design references.
// The current UI stage uses PNG assets directly to stay close to approved mockups.
object DesignAssets {
    const val Splash = "design/Splash.png"
    const val SignIn = "design/Sign in.png"
    const val RegisterAccount = "design/Register Account.png"
    const val ForgotPassword = "design/Forgot Password.png"
    const val Popup = "design/Popup.png"
    const val Verification = "design/Verification.png"
    const val CreateNewPassword = "design/Create New Password.png"
    const val Home = "design/Home.png"
    const val Stock = "design/Stock.png"
    const val Catalog = "design/Catalog.png"
    const val CardItem = "design/Card Item.png"
    const val Favorite = "design/Favorite.png"
    const val Profile = "design/Profile.png"
    const val EditProfile = "design/Edit Profile.png"
    const val LoyaltyCard = "design/Loyalty Card.png"
    const val LoyaltyCardItem = "Loyalty Card item.png"
    const val SideMenu = "design/Side Menu.png"
    const val StyleGuide = "design/Style Guide.png"
    const val IconSvg = "design/Icon.svg"
    const val AvatarSvg = "design/Avatar Test.svg"
}

object HomeVisualTuning {
    // These values pin the current visual calibration of Home cards/promo blocks.
    // Keep them centralized so UI polish does not require hunting through screen code.
    const val promotionHeightDp = 108
    const val productImageContainerSizeDp = 132
    const val productImageSizeDp = 124
    const val productImageOffsetXDp = -6
    const val productTitleLineCount = 2
}

@Composable
fun DesignPngAsset(
    assetPath: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val context = LocalContext.current
    val bitmap = remember(assetPath) {
        // Missing asset should fail soft in previews/UI review instead of crashing the host screen.
        runCatching {
            context.assets.open(assetPath).use(BitmapFactory::decodeStream)
        }.getOrNull()
    }
    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
        )
    }
}
