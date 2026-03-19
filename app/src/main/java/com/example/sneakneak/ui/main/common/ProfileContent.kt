package com.example.sneakneak.ui.main.common

// Переиспользуемые UI-блоки profile/loyalty экранов.
// Содержит header, поля профиля и визуализацию штрихкода.

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.sneakneak.ui.components.AppIcon
import com.example.sneakneak.ui.components.AppIconAsset
import com.example.sneakneak.ui.components.AppTextField
import com.example.sneakneak.ui.components.ProfileAvatar
import com.example.sneakneak.ui.theme.AppColors

@Composable
fun ProfileHeaderBlock(
    fullName: String,
    modifier: Modifier = Modifier,
    avatarUrl: String? = null,
    subtitle: String? = null,
    onSubtitleClick: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProfileAvatar(
            name = fullName,
            imageUrl = avatarUrl,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = fullName,
            style = MaterialTheme.typography.headlineLarge,
            color = AppColors.TextPrimary,
        )
        if (subtitle != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                modifier = if (onSubtitleClick != null) {
                    Modifier.clickable(onClick = onSubtitleClick)
                } else {
                    Modifier
                },
                color = AppColors.Primary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
fun ProfileBarcodeCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Карта лояльности",
                    style = MaterialTheme.typography.titleMedium,
                    color = AppColors.TextPrimary,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Открыть штрихкод",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.TextSecondary,
                )
            }
            Box(
                modifier = Modifier
                    .height(52.dp)
                    .width(88.dp)
                    .background(AppColors.SurfaceVariant, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center,
            ) {
                MiniBarcodePlaceholder()
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .background(AppColors.SurfaceVariant, CircleShape)
                    .padding(10.dp),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(
                    asset = AppIconAsset.ChevronRight,
                    contentDescription = "Открыть карту",
                    tint = AppColors.TextPrimary,
                )
            }
        }
    }
}

@Composable
fun ProfileFieldList(
    firstName: String,
    lastName: String,
    address: String,
    phone: String,
    editable: Boolean,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
) {
    ProfileField(
        label = "Имя",
        value = firstName,
        editable = editable,
        onValueChange = onFirstNameChange,
    )
    Spacer(modifier = Modifier.height(18.dp))
    ProfileField(
        label = "Фамилия",
        value = lastName,
        editable = editable,
        onValueChange = onLastNameChange,
    )
    Spacer(modifier = Modifier.height(18.dp))
    ProfileField(
        label = "Адрес",
        value = address,
        editable = editable,
        onValueChange = onAddressChange,
    )
    Spacer(modifier = Modifier.height(18.dp))
    ProfileField(
        label = "Телефон",
        value = phone,
        editable = editable,
        onValueChange = onPhoneChange,
    )
}

@Composable
private fun MiniBarcodePlaceholder() {
    // Превью в карточке профиля является стилизованной имитацией, реальный штрихкод строится на Loyalty screen.
    Row(
        modifier = Modifier
            .height(42.dp)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val pattern = listOf(2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2)
        pattern.forEachIndexed { index, widthUnits ->
            Box(
                modifier = Modifier
                    .height(if (index % 4 == 0) 40.dp else 28.dp)
                    .width((widthUnits + 1).dp)
                    .background(AppColors.TextPrimary),
            )
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    editable: Boolean,
    onValueChange: (String) -> Unit,
) {
    AppTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        placeholder = label,
        readOnly = !editable,
        trailingIcon = if (editable) {
            {
                AppIcon(
                    asset = AppIconAsset.Edit,
                    contentDescription = null,
                    tint = AppColors.Primary,
                )
            }
        } else null,
    )
}

@Composable
fun LoyaltyBarcodeBlock(
    modifier: Modifier = Modifier,
    barcodeImage: ImageBitmap? = null,
    barcodeLabel: String? = null,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(610.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
    ) {
        val contentHorizontalPadding = if (barcodeImage != null) 6.dp else 18.dp
        val contentVerticalPadding = if (barcodeImage != null) 4.dp else 24.dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = contentHorizontalPadding, vertical = contentVerticalPadding),
            contentAlignment = Alignment.Center,
        ) {
            if (barcodeImage != null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.98f),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            bitmap = barcodeImage,
                            contentDescription = "Штрихкод карты лояльности",
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    rotationZ = 90f
                                    scaleX = 2.4f
                                    scaleY = 1.58f
                                },
                            contentScale = ContentScale.FillBounds,
                        )
                    }
                    if (!barcodeLabel.isNullOrBlank()) {
                        Text(
                            text = barcodeLabel,
                            modifier = Modifier.padding(top = 10.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.TextSecondary,
                        )
                    }
                }
            }
        }
    }
}
