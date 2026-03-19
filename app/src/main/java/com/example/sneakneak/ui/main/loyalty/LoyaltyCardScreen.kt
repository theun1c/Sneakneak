package com.example.sneakneak.ui.main.loyalty

// Экран Loyalty Card + ViewModel.
// Формирует barcode из auth user id и показывает его в выделенном макете.

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.sneakneak.core.barcode.Code128BarcodeGenerator
import com.example.sneakneak.di.AppContainer
import com.example.sneakneak.domain.loyalty.model.LoyaltyResult
import com.example.sneakneak.domain.loyalty.usecase.LoyaltyUseCases
import com.example.sneakneak.ui.components.InfoDialog
import com.example.sneakneak.ui.main.common.LoyaltyBarcodeBlock
import com.example.sneakneak.ui.main.common.MainShellScaffold
import com.example.sneakneak.ui.main.common.MainTopBarStyle
import com.example.sneakneak.ui.navigation.AppRoutes
import com.example.sneakneak.ui.theme.AppColors
import com.example.sneakneak.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LoyaltyCardUiState(
    val title: String = "Карта лояльности",
    val displayName: String = "",
    val barcodePayload: String = "",
    val barcodeBitmap: Bitmap? = null,
    val isLoading: Boolean = true,
    val dialogMessage: String? = null,
)

sealed interface LoyaltyCardUiEvent {
    data object BackClicked : LoyaltyCardUiEvent
    data object RetryClicked : LoyaltyCardUiEvent
    data object DialogDismissed : LoyaltyCardUiEvent
}

class LoyaltyCardViewModel(
    private val useCases: LoyaltyUseCases,
    private val barcodeGenerator: Code128BarcodeGenerator,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    var uiState by mutableStateOf(LoyaltyCardUiState())
        private set

    init {
        loadCard()
    }

    fun onEvent(event: LoyaltyCardUiEvent) {
        when (event) {
            LoyaltyCardUiEvent.BackClicked -> Unit
            LoyaltyCardUiEvent.RetryClicked -> loadCard()
            LoyaltyCardUiEvent.DialogDismissed -> uiState = uiState.copy(dialogMessage = null)
        }
    }

    private fun loadCard() {
        scope.launch {
            uiState = uiState.copy(isLoading = true, dialogMessage = null)
            when (val result = useCases.getLoyaltyCardInfo()) {
                is LoyaltyResult.Error -> {
                    uiState = uiState.copy(
                        isLoading = false,
                        dialogMessage = result.message,
                    )
                }

                is LoyaltyResult.Success -> {
                    // Генерацию bitmap выносим в Default dispatcher, чтобы не блокировать main thread.
                    val payload = result.data.userId
                    val barcode = withContext(Dispatchers.Default) {
                        barcodeGenerator.generate(payload = payload)
                    }
                    uiState = uiState.copy(
                        displayName = result.data.displayName,
                        barcodePayload = payload,
                        barcodeBitmap = barcode,
                        isLoading = false,
                        dialogMessage = null,
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
fun LoyaltyCardRoute(
    currentRoute: String,
    onBottomNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: LoyaltyCardViewModel = remember {
        LoyaltyCardViewModel(
            useCases = AppContainer.loyaltyUseCases,
            barcodeGenerator = Code128BarcodeGenerator(),
        )
    },
) {
    LoyaltyCardScreen(
        state = viewModel.uiState,
        currentRoute = currentRoute,
        onEvent = viewModel::onEvent,
        onBottomNavigate = onBottomNavigate,
        onBack = onBack,
    )
}

@Composable
fun LoyaltyCardScreen(
    state: LoyaltyCardUiState,
    currentRoute: String,
    onEvent: (LoyaltyCardUiEvent) -> Unit,
    onBottomNavigate: (String) -> Unit,
    onBack: () -> Unit,
) {
    MainShellScaffold(
        currentRoute = currentRoute,
        topBarStyle = MainTopBarStyle.BackTitleAction(title = state.title),
        onBottomItemClick = { onBottomNavigate(it.route) },
        onDrawerItemClick = {},
        onBackClick = {
            onEvent(LoyaltyCardUiEvent.BackClicked)
            onBack()
        },
    ) { modifier ->
        Box(modifier = modifier) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 40.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (state.displayName.isNotBlank()) {
                    Text(
                        text = state.displayName,
                        style = MaterialTheme.typography.titleLarge,
                        color = AppColors.TextPrimary,
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                LoyaltyBarcodeBlock(
                    barcodeImage = state.barcodeBitmap?.asImageBitmap(),
                    barcodeLabel = null,
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
            onDismiss = { onEvent(LoyaltyCardUiEvent.DialogDismissed) },
        )
    }
}

private val loyaltyPreviewState = LoyaltyCardUiState(
    displayName = "Emmanuel Oyiboke",
    barcodePayload = "f5f0d97d-6f19-4b14-8ce2-b9cdaf0f95a2",
    isLoading = false,
)

@Preview
@Composable
private fun LoyaltyPreview() {
    AppTheme {
        LoyaltyCardScreen(
            state = loyaltyPreviewState,
            currentRoute = AppRoutes.LoyaltyCard.route,
            onEvent = {},
            onBottomNavigate = {},
            onBack = {},
        )
    }
}
