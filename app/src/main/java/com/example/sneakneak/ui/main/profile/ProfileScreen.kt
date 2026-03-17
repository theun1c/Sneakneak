package com.example.sneakneak.ui.main.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.sneakneak.ui.components.AppIconAsset
import com.example.sneakneak.ui.main.common.MainShellScaffold
import com.example.sneakneak.ui.main.common.MainTopBarStyle
import com.example.sneakneak.ui.main.common.ProfileBarcodeCard
import com.example.sneakneak.ui.main.common.ProfileFieldList
import com.example.sneakneak.ui.main.common.ProfileHeaderBlock
import com.example.sneakneak.ui.navigation.AppRoutes
import com.example.sneakneak.ui.theme.AppColors
import com.example.sneakneak.ui.theme.AppTheme

// Profile screen is wired for navigation/edit/barcode actions, while user data is still mock-filled.
data class ProfileUiState(
    val fullName: String = "Emmanuel Oyiboke",
    val firstName: String = "Emmanuel",
    val lastName: String = "Oyiboke",
    val address: String = "Nigeria",
    val phone: String = "",
)

sealed interface ProfileUiEvent {
    data object BarcodeClicked : ProfileUiEvent
    data object EditClicked : ProfileUiEvent
}

class ProfileViewModel : ViewModel() {
    var uiState by mutableStateOf(ProfileUiState())
        private set

    fun onEvent(event: ProfileUiEvent) {
        when (event) {
            // TODO(DATA): replace static profile state with profile repository/use case loading.
            ProfileUiEvent.BarcodeClicked, ProfileUiEvent.EditClicked -> Unit
        }
    }
}

@Composable
fun ProfileRoute(
    currentRoute: String,
    onBottomNavigate: (String) -> Unit,
    onDrawerNavigate: (String) -> Unit,
    onEditClick: () -> Unit,
    onBarcodeClick: () -> Unit,
    viewModel: ProfileViewModel = remember { ProfileViewModel() },
) {
    ProfileScreen(
        state = viewModel.uiState,
        currentRoute = currentRoute,
        onEvent = viewModel::onEvent,
        onBottomNavigate = onBottomNavigate,
        onDrawerNavigate = onDrawerNavigate,
        onEditClick = onEditClick,
        onBarcodeClick = onBarcodeClick,
    )
}

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    currentRoute: String,
    onEvent: (ProfileUiEvent) -> Unit,
    onBottomNavigate: (String) -> Unit,
    onDrawerNavigate: (String) -> Unit,
    onEditClick: () -> Unit,
    onBarcodeClick: () -> Unit,
) {
    MainShellScaffold(
        currentRoute = currentRoute,
        topBarStyle = MainTopBarStyle.MenuTitleAction(
            title = "Профиль",
            actionIcon = AppIconAsset.Edit,
            actionTint = AppColors.Primary,
        ),
        onBottomItemClick = { onBottomNavigate(it.route) },
        onDrawerItemClick = { onDrawerNavigate(it.route) },
        onTopActionClick = {
            onEvent(ProfileUiEvent.EditClicked)
            onEditClick()
        },
    ) { modifier ->
        Column(
            modifier = modifier
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProfileHeaderBlock(
                fullName = state.fullName,
                subtitle = "Покупатель Silver",
            )
            Spacer(modifier = Modifier.height(20.dp))
            ProfileBarcodeCard(
                onClick = {
                    onEvent(ProfileUiEvent.BarcodeClicked)
                    onBarcodeClick()
                },
            )
            Spacer(modifier = Modifier.height(18.dp))
            ProfileFieldList(
                firstName = state.firstName,
                lastName = state.lastName,
                address = state.address,
                phone = state.phone,
                editable = false,
                onFirstNameChange = {},
                onLastNameChange = {},
                onAddressChange = {},
                onPhoneChange = {},
            )
        }
    }
}

private val profilePreviewState = ProfileUiState(
    phone = "+7 811-732-5298",
)

@Preview
@Composable
private fun ProfilePreview() {
    AppTheme {
        ProfileScreen(
            state = profilePreviewState,
            currentRoute = AppRoutes.Profile.route,
            onEvent = {},
            onBottomNavigate = {},
            onDrawerNavigate = {},
            onEditClick = {},
            onBarcodeClick = {},
        )
    }
}
