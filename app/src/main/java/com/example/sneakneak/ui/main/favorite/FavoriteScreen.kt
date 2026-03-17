package com.example.sneakneak.ui.main.favorite

import androidx.compose.foundation.layout.Column
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
import com.example.sneakneak.ui.components.AppIconAsset
import com.example.sneakneak.ui.main.common.FavoriteEmptyState
import com.example.sneakneak.ui.main.common.MockCatalogState
import com.example.sneakneak.ui.main.common.MainShellScaffold
import com.example.sneakneak.ui.main.common.MainTopBarStyle
import com.example.sneakneak.ui.main.common.ProductsGrid
import com.example.sneakneak.ui.navigation.AppRoutes
import com.example.sneakneak.ui.theme.AppTheme

// Favorite screen currently demonstrates empty/content UI states against local memory state.
data class FavoriteUiState(
    val title: String = "Избранное",
)

sealed interface FavoriteUiEvent {
    data object BackClicked : FavoriteUiEvent
}

class FavoriteViewModel : ViewModel() {
    var uiState by mutableStateOf(FavoriteUiState())
        private set

    fun onEvent(event: FavoriteUiEvent) {
        when (event) {
            FavoriteUiEvent.BackClicked -> Unit
        }
    }
}

@Composable
fun FavoriteRoute(
    currentRoute: String,
    onBottomNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: FavoriteViewModel = remember { FavoriteViewModel() },
) {
    FavoriteScreen(
        state = viewModel.uiState,
        currentRoute = currentRoute,
        onEvent = viewModel::onEvent,
        onBottomNavigate = onBottomNavigate,
        onBack = onBack,
    )
}

@Composable
fun FavoriteScreen(
    state: FavoriteUiState,
    currentRoute: String,
    onEvent: (FavoriteUiEvent) -> Unit,
    onBottomNavigate: (String) -> Unit,
    onBack: () -> Unit,
) {
    MainShellScaffold(
        currentRoute = currentRoute,
        topBarStyle = MainTopBarStyle.BackTitleAction(
            title = state.title,
            actionIcon = AppIconAsset.HeartFilled,
            actionTint = com.example.sneakneak.ui.theme.AppColors.Accent,
        ),
        onBottomItemClick = { onBottomNavigate(it.route) },
        onDrawerItemClick = {},
        onBackClick = {
            onEvent(FavoriteUiEvent.BackClicked)
            onBack()
        },
    ) { modifier ->
        Column(modifier = modifier.padding(horizontal = 20.dp)) {
            // TODO(DATA): load only current-user favorites from repository once data layer is connected.
            val favoriteProducts = MockCatalogState.favoriteProducts()
            if (favoriteProducts.isEmpty()) {
                FavoriteEmptyState()
            } else {
                ProductsGrid(
                    products = favoriteProducts,
                    onFavoriteClick = MockCatalogState::toggleFavorite,
                )
            }
        }
    }
}

private val favoritePreviewState = FavoriteUiState()

@Preview
@Composable
private fun FavoritePreview() {
    MockCatalogState.seedFavoritesForPreview()
    AppTheme {
        FavoriteScreen(
            state = favoritePreviewState,
            currentRoute = AppRoutes.Favorite.route,
            onEvent = {},
            onBottomNavigate = {},
            onBack = {},
        )
    }
}
