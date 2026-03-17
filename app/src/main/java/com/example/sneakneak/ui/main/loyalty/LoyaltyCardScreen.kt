package com.example.sneakneak.ui.main.loyalty

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.sneakneak.ui.main.common.LoyaltyBarcodeBlock
import com.example.sneakneak.ui.main.common.MainShellScaffold
import com.example.sneakneak.ui.main.common.MainTopBarStyle
import com.example.sneakneak.ui.navigation.AppRoutes
import com.example.sneakneak.ui.theme.AppTheme

// Loyalty card is visually ready, but barcode payload is still a presentation placeholder.
data class LoyaltyCardUiState(
    val title: String = "Карта лояльности",
)

sealed interface LoyaltyCardUiEvent {
    data object BackClicked : LoyaltyCardUiEvent
}

class LoyaltyCardViewModel : ViewModel() {
    var uiState by mutableStateOf(LoyaltyCardUiState())
        private set

    fun onEvent(event: LoyaltyCardUiEvent) {
        when (event) {
            // TODO(DATA): feed barcode/user payload from auth/profile domain data.
            LoyaltyCardUiEvent.BackClicked -> Unit
        }
    }
}

@Composable
fun LoyaltyCardRoute(
    currentRoute: String,
    onBottomNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: LoyaltyCardViewModel = remember { LoyaltyCardViewModel() },
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
        LoyaltyBarcodeBlock(
            modifier = modifier.padding(horizontal = 40.dp, vertical = 24.dp),
        )
    }
}

private val loyaltyPreviewState = LoyaltyCardUiState()

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
