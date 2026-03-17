package com.example.sneakneak.ui.main.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
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
    subtitle: String? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ProfileAvatar(name = fullName)
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
            Row(
                modifier = Modifier
                    .height(44.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(16) { index ->
                    Box(
                        modifier = Modifier
                            .height(if (index % 3 == 0) 42.dp else 28.dp)
                            .width(if (index % 2 == 0) 3.dp else 2.dp)
                            .background(Color.Black),
                    )
                }
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
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(610.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = AppColors.Surface),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.82f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                repeat(32) { rowIndex ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(
                                when {
                                    rowIndex % 7 == 0 -> 0.88f
                                    rowIndex % 5 == 0 -> 0.96f
                                    else -> 1f
                                }
                            )
                            .align(
                                when {
                                    rowIndex % 6 == 0 -> Alignment.End
                                    rowIndex % 4 == 0 -> Alignment.Start
                                    else -> Alignment.CenterHorizontally
                                }
                            )
                            .height(
                                when {
                                    rowIndex % 4 == 0 -> 10.dp
                                    rowIndex % 3 == 0 -> 8.dp
                                    else -> 6.dp
                                }
                            )
                            .background(Color.Black),
                    )
                }
            }
        }
    }
}
