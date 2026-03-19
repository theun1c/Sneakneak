package com.example.sneakneak.ui.main.profile

// Экран просмотра профиля + ViewModel.
// Загружает профиль текущего пользователя и отображает ссылку на Loyalty Card и редактирование.

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import com.example.sneakneak.di.AppContainer
import com.example.sneakneak.domain.profile.model.ProfileResult
import com.example.sneakneak.domain.profile.model.UserProfile
import com.example.sneakneak.domain.profile.usecase.ProfileUseCases
import com.example.sneakneak.ui.components.AppIconAsset
import com.example.sneakneak.ui.components.InfoDialog
import com.example.sneakneak.ui.main.common.MainShellScaffold
import com.example.sneakneak.ui.main.common.MainTopBarStyle
import com.example.sneakneak.ui.main.common.ProfileBarcodeCard
import com.example.sneakneak.ui.main.common.ProfileFieldList
import com.example.sneakneak.ui.main.common.ProfileHeaderBlock
import com.example.sneakneak.ui.navigation.AppRoutes
import com.example.sneakneak.ui.theme.AppColors
import com.example.sneakneak.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

data class ProfileUiState(
    val fullName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val avatarUrl: String? = null,
    val isLoading: Boolean = true,
    val dialogMessage: String? = null,
)

sealed interface ProfileUiEvent {
    data object BarcodeClicked : ProfileUiEvent
    data object EditClicked : ProfileUiEvent
    data object DialogDismissed : ProfileUiEvent
    data object RetryClicked : ProfileUiEvent
}

class ProfileViewModel(
    private val useCases: ProfileUseCases,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        loadProfile()
    }

    fun onEvent(event: ProfileUiEvent) {
        when (event) {
            ProfileUiEvent.BarcodeClicked, ProfileUiEvent.EditClicked -> Unit
            ProfileUiEvent.DialogDismissed -> uiState = uiState.copy(dialogMessage = null)
            ProfileUiEvent.RetryClicked -> loadProfile()
        }
    }

    fun refresh() {
        loadProfile()
    }

    private fun loadProfile() {
        scope.launch {
            uiState = uiState.copy(isLoading = true, dialogMessage = null)
            when (val result = useCases.createOrGetMyProfile()) {
                is ProfileResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        dialogMessage = result.message,
                    )
                }

                is ProfileResult.Success -> {
                    uiState = result.data.toUiState()
                }
            }
        }
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}

@Composable
fun ProfileRoute(
    currentRoute: String,
    onBottomNavigate: (String) -> Unit,
    onDrawerNavigate: (String) -> Unit,
    onEditClick: () -> Unit,
    onBarcodeClick: () -> Unit,
    viewModel: ProfileViewModel = remember { ProfileViewModel(AppContainer.profileUseCases) },
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, viewModel) {
        // При возврате с Edit Profile обновляем данные, чтобы UI всегда отражал актуальный профиль/аватар.
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
        Box(modifier = modifier) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ProfileHeaderBlock(
                    fullName = state.fullName,
                    avatarUrl = state.avatarUrl,
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

            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AppColors.Primary,
                )
            }
        }
    }

    state.dialogMessage?.let {
        InfoDialog(
            title = "Ошибка",
            message = it,
            onDismiss = { onEvent(ProfileUiEvent.DialogDismissed) },
        )
    }
}

private fun UserProfile.toUiState(): ProfileUiState {
    val mergedName = listOf(firstname, lastname)
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank {
            email?.substringBefore("@")?.takeIf { it.isNotBlank() } ?: "Пользователь"
        }
    return ProfileUiState(
        fullName = mergedName,
        firstName = firstname,
        lastName = lastname,
        address = address,
        phone = phone,
        email = email.orEmpty(),
        avatarUrl = photo,
        isLoading = false,
        dialogMessage = null,
    )
}

private val profilePreviewState = ProfileUiState(
    fullName = "Emmanuel Oyiboke",
    firstName = "Emmanuel",
    lastName = "Oyiboke",
    address = "Nigeria",
    phone = "+7 811-732-5298",
    avatarUrl = null,
    isLoading = false,
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
