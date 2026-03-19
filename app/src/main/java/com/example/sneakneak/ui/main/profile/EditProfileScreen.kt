package com.example.sneakneak.ui.main.profile

// Экран редактирования профиля + ViewModel.
// Поддерживает сохранение полей профиля и обновление аватара (галерея/камера -> upload -> profile.photo).

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.sneakneak.core.media.AvatarImageTransformer
import com.example.sneakneak.di.AppContainer
import com.example.sneakneak.domain.profile.model.ProfileResult
import com.example.sneakneak.domain.profile.model.UserProfile
import com.example.sneakneak.domain.profile.model.UserProfileDraft
import com.example.sneakneak.domain.profile.usecase.ProfileUseCases
import com.example.sneakneak.ui.components.InfoDialog
import com.example.sneakneak.ui.main.common.MainShellScaffold
import com.example.sneakneak.ui.main.common.MainTopBarStyle
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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class EditProfileUiState(
    val fullName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val phone: String = "",
    val avatarUrl: String? = null,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isAvatarUploading: Boolean = false,
    val isAvatarSourceDialogVisible: Boolean = false,
    val dialogTitle: String? = null,
    val dialogMessage: String? = null,
)

sealed interface EditProfileUiEvent {
    data class FirstNameChanged(val value: String) : EditProfileUiEvent
    data class LastNameChanged(val value: String) : EditProfileUiEvent
    data class AddressChanged(val value: String) : EditProfileUiEvent
    data class PhoneChanged(val value: String) : EditProfileUiEvent
    data class AvatarBytesReady(val bytes: ByteArray) : EditProfileUiEvent
    data object SaveClicked : EditProfileUiEvent
    data object ChangePhotoClicked : EditProfileUiEvent
    data object PickFromGalleryClicked : EditProfileUiEvent
    data object TakePhotoClicked : EditProfileUiEvent
    data object AvatarSourceDialogDismissed : EditProfileUiEvent
    data object AvatarPickFailed : EditProfileUiEvent
    data object BackClicked : EditProfileUiEvent
    data object DialogDismissed : EditProfileUiEvent
}

sealed interface EditProfileUiEffect {
    data object LaunchGalleryPicker : EditProfileUiEffect
    data object LaunchCameraCapture : EditProfileUiEffect
}

class EditProfileViewModel(
    private val useCases: ProfileUseCases,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val _effects = MutableSharedFlow<EditProfileUiEffect>()
    val effects: SharedFlow<EditProfileUiEffect> = _effects.asSharedFlow()

    var uiState by mutableStateOf(EditProfileUiState())
        private set

    init {
        loadProfile()
    }

    fun onEvent(event: EditProfileUiEvent) {
        when (event) {
            is EditProfileUiEvent.FirstNameChanged -> {
                val nextFirst = event.value
                uiState = uiState.copy(
                    firstName = nextFirst,
                    fullName = buildFullName(nextFirst, uiState.lastName),
                )
            }

            is EditProfileUiEvent.LastNameChanged -> {
                val nextLast = event.value
                uiState = uiState.copy(
                    lastName = nextLast,
                    fullName = buildFullName(uiState.firstName, nextLast),
                )
            }

            is EditProfileUiEvent.AddressChanged -> uiState = uiState.copy(address = event.value)
            is EditProfileUiEvent.PhoneChanged -> uiState = uiState.copy(phone = event.value)
            is EditProfileUiEvent.AvatarBytesReady -> updateAvatar(event.bytes)
            EditProfileUiEvent.AvatarPickFailed -> {
                uiState = uiState.copy(
                    dialogTitle = "Ошибка",
                    dialogMessage = "Не удалось обработать изображение",
                )
            }

            EditProfileUiEvent.ChangePhotoClicked -> {
                uiState = uiState.copy(isAvatarSourceDialogVisible = true)
            }

            EditProfileUiEvent.PickFromGalleryClicked -> {
                uiState = uiState.copy(isAvatarSourceDialogVisible = false)
                scope.launch { _effects.emit(EditProfileUiEffect.LaunchGalleryPicker) }
            }

            EditProfileUiEvent.TakePhotoClicked -> {
                uiState = uiState.copy(isAvatarSourceDialogVisible = false)
                scope.launch { _effects.emit(EditProfileUiEffect.LaunchCameraCapture) }
            }

            EditProfileUiEvent.AvatarSourceDialogDismissed -> {
                uiState = uiState.copy(isAvatarSourceDialogVisible = false)
            }

            EditProfileUiEvent.BackClicked -> Unit
            EditProfileUiEvent.DialogDismissed -> {
                uiState = uiState.copy(
                    dialogTitle = null,
                    dialogMessage = null,
                )
            }

            EditProfileUiEvent.SaveClicked -> saveProfile()
        }
    }

    private fun loadProfile() {
        scope.launch {
            uiState = uiState.copy(
                isLoading = true,
                dialogTitle = null,
                dialogMessage = null,
            )
            when (val result = useCases.getCurrentUserProfile()) {
                is ProfileResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        dialogTitle = "Ошибка",
                        dialogMessage = result.message,
                    )
                }

                is ProfileResult.Success -> {
                    uiState = result.data.toEditState(
                        isSaving = false,
                        isAvatarUploading = false,
                    )
                }
            }
        }
    }

    private fun saveProfile() {
        scope.launch {
            if (uiState.isLoading || uiState.isSaving || uiState.isAvatarUploading) return@launch
            uiState = uiState.copy(isSaving = true)
            when (
                val result = useCases.updateMyProfile(
                    UserProfileDraft(
                        firstname = uiState.firstName,
                        lastname = uiState.lastName,
                        address = uiState.address,
                        phone = uiState.phone,
                        photo = uiState.avatarUrl,
                    ),
                )
            ) {
                is ProfileResult.Error -> {
                    uiState = uiState.copy(
                        isSaving = false,
                        dialogTitle = "Ошибка",
                        dialogMessage = result.message,
                    )
                }

                is ProfileResult.Success -> {
                    uiState = result.data.toEditState(
                        isSaving = false,
                        isAvatarUploading = false,
                    ).copy(
                        dialogTitle = "Успешно",
                        dialogMessage = "Профиль сохранён",
                    )
                }
            }
        }
    }

    private fun updateAvatar(imageBytes: ByteArray) {
        scope.launch {
            if (uiState.isLoading || uiState.isAvatarUploading) return@launch
            uiState = uiState.copy(isAvatarUploading = true)
            // Вызов use case инкапсулирует storage upload и update записи profiles.
            when (val result = useCases.updateMyAvatar(imageBytes)) {
                is ProfileResult.Error -> {
                    uiState = uiState.copy(
                        isAvatarUploading = false,
                        dialogTitle = "Ошибка",
                        dialogMessage = result.message,
                    )
                }

                is ProfileResult.Success -> {
                    uiState = result.data.toEditState(
                        isSaving = false,
                        isAvatarUploading = false,
                    )
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
fun EditProfileRoute(
    currentRoute: String,
    onBottomNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: EditProfileViewModel = remember { EditProfileViewModel(AppContainer.profileUseCases) },
) {
    val context = LocalContext.current
    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        val bytes = AvatarImageTransformer.uriToPngBytes(context, uri)
        if (bytes == null) {
            viewModel.onEvent(EditProfileUiEvent.AvatarPickFailed)
        } else {
            viewModel.onEvent(EditProfileUiEvent.AvatarBytesReady(bytes))
        }
    }
    val cameraCapture = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
    ) { bitmap ->
        if (bitmap == null) return@rememberLauncherForActivityResult
        val bytes = AvatarImageTransformer.bitmapToPngBytes(bitmap)
        viewModel.onEvent(EditProfileUiEvent.AvatarBytesReady(bytes))
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                EditProfileUiEffect.LaunchGalleryPicker -> {
                    galleryPicker.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                    )
                }

                EditProfileUiEffect.LaunchCameraCapture -> {
                    cameraCapture.launch(null)
                }
            }
        }
    }

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
        Box(modifier = modifier) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ProfileHeaderBlock(
                    fullName = state.fullName,
                    avatarUrl = state.avatarUrl,
                    subtitle = "Изменить фото профиля",
                    onSubtitleClick = { onEvent(EditProfileUiEvent.ChangePhotoClicked) },
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

            if (state.isLoading || state.isSaving || state.isAvatarUploading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AppColors.Primary,
                )
            }
        }
    }

    if (state.isAvatarSourceDialogVisible) {
        AvatarSourceDialog(
            onTakePhoto = { onEvent(EditProfileUiEvent.TakePhotoClicked) },
            onPickGallery = { onEvent(EditProfileUiEvent.PickFromGalleryClicked) },
            onDismiss = { onEvent(EditProfileUiEvent.AvatarSourceDialogDismissed) },
        )
    }

    if (state.dialogTitle != null && state.dialogMessage != null) {
        InfoDialog(
            title = state.dialogTitle,
            message = state.dialogMessage,
            onDismiss = { onEvent(EditProfileUiEvent.DialogDismissed) },
        )
    }
}

@Composable
private fun AvatarSourceDialog(
    onTakePhoto: () -> Unit,
    onPickGallery: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Фото профиля") },
        text = {
            Column {
                TextButton(onClick = onTakePhoto) {
                    Text("Сделать фото")
                }
                TextButton(onClick = onPickGallery) {
                    Text("Выбрать из галереи")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        },
    )
}

private fun UserProfile.toEditState(
    isSaving: Boolean,
    isAvatarUploading: Boolean,
): EditProfileUiState {
    return EditProfileUiState(
        fullName = buildFullName(firstname, lastname, email),
        firstName = firstname,
        lastName = lastname,
        address = address,
        phone = phone,
        avatarUrl = photo,
        isLoading = false,
        isSaving = isSaving,
        isAvatarUploading = isAvatarUploading,
        isAvatarSourceDialogVisible = false,
        dialogTitle = null,
        dialogMessage = null,
    )
}

private fun buildFullName(
    firstName: String,
    lastName: String,
): String {
    return listOf(firstName, lastName)
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { "Пользователь" }
}

private fun buildFullName(
    firstName: String,
    lastName: String,
    email: String?,
): String {
    return listOf(firstName, lastName)
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { email?.substringBefore("@")?.takeIf { it.isNotBlank() } ?: "Пользователь" }
}

private val editProfilePreviewState = EditProfileUiState(
    fullName = "Emmanuel Oyiboke",
    firstName = "Emmanuel",
    lastName = "Oyiboke",
    address = "Nigeria",
    phone = "+7 811-732-5298",
    avatarUrl = null,
    isLoading = false,
    isSaving = false,
    isAvatarUploading = false,
)

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
