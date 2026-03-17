package com.example.sneakneak.ui.main.profile

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
import com.example.sneakneak.ui.main.common.MainShellScaffold
import com.example.sneakneak.ui.main.common.MainTopBarStyle
import com.example.sneakneak.ui.main.common.ProfileFieldList
import com.example.sneakneak.ui.main.common.ProfileHeaderBlock
import com.example.sneakneak.ui.navigation.AppRoutes
import com.example.sneakneak.ui.theme.AppTheme

// Edit profile currently proves the editable UI contract and local field synchronization.
data class EditProfileUiState(
    val fullName: String = "Emmanuel Oyiboke",
    val firstName: String = "Emmanuel",
    val lastName: String = "Oyiboke",
    val address: String = "Nigeria",
    val phone: String = "+7 811-732-5298",
)

sealed interface EditProfileUiEvent {
    data class FirstNameChanged(val value: String) : EditProfileUiEvent
    data class LastNameChanged(val value: String) : EditProfileUiEvent
    data class AddressChanged(val value: String) : EditProfileUiEvent
    data class PhoneChanged(val value: String) : EditProfileUiEvent
    data object SaveClicked : EditProfileUiEvent
    data object BackClicked : EditProfileUiEvent
}

class EditProfileViewModel : ViewModel() {
    var uiState by mutableStateOf(EditProfileUiState())
        private set

    fun onEvent(event: EditProfileUiEvent) {
        when (event) {
            is EditProfileUiEvent.FirstNameChanged -> uiState = uiState.copy(
                firstName = event.value,
                fullName = "${event.value} ${uiState.lastName}".trim()
            )
            is EditProfileUiEvent.LastNameChanged -> uiState = uiState.copy(
                lastName = event.value,
                fullName = "${uiState.firstName} ${event.value}".trim()
            )
            is EditProfileUiEvent.AddressChanged -> uiState = uiState.copy(address = event.value)
            is EditProfileUiEvent.PhoneChanged -> uiState = uiState.copy(phone = event.value)
            // TODO(DATA): persist edits and avatar changes through profile use cases.
            EditProfileUiEvent.SaveClicked, EditProfileUiEvent.BackClicked -> Unit
        }
    }
}

@Composable
fun EditProfileRoute(
    currentRoute: String,
    onBottomNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: EditProfileViewModel = remember { EditProfileViewModel() },
) {
    EditProfileScreen(
        state = viewModel.uiState,
        currentRoute = currentRoute,
        onEvent = viewModel::onEvent,
        onBottomNavigate = onBottomNavigate,
        onBack = onBack,
    )
}

@Composable
fun EditProfileScreen(
    state: EditProfileUiState,
    currentRoute: String,
    onEvent: (EditProfileUiEvent) -> Unit,
    onBottomNavigate: (String) -> Unit,
    onBack: () -> Unit,
) {
    MainShellScaffold(
        currentRoute = currentRoute,
        topBarStyle = MainTopBarStyle.CenterButton(buttonText = "Сохранить"),
        onBottomItemClick = { onBottomNavigate(it.route) },
        onDrawerItemClick = {},
        showBottomBar = true,
        onBackClick = {
            onEvent(EditProfileUiEvent.BackClicked)
            onBack()
        },
        onTopActionClick = { onEvent(EditProfileUiEvent.SaveClicked) },
    ) { modifier ->
        androidx.compose.foundation.layout.Column(
            modifier = modifier.padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProfileHeaderBlock(
                fullName = state.fullName,
                subtitle = "Изменить фото профиля",
            )
            Spacer(modifier = Modifier.height(24.dp))
            ProfileFieldList(
                firstName = state.firstName,
                lastName = state.lastName,
                address = state.address,
                phone = state.phone,
                editable = true,
                onFirstNameChange = { onEvent(EditProfileUiEvent.FirstNameChanged(it)) },
                onLastNameChange = { onEvent(EditProfileUiEvent.LastNameChanged(it)) },
                onAddressChange = { onEvent(EditProfileUiEvent.AddressChanged(it)) },
                onPhoneChange = { onEvent(EditProfileUiEvent.PhoneChanged(it)) },
            )
        }
    }
}

private val editProfilePreviewState = EditProfileUiState()

@Preview
@Composable
private fun EditProfilePreview() {
    AppTheme {
        EditProfileScreen(
            state = editProfilePreviewState,
            currentRoute = AppRoutes.Profile.route,
            onEvent = {},
            onBottomNavigate = {},
            onBack = {},
        )
    }
}
