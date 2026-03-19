package com.example.sneakneak.ui.main.favorite

// Экран избранного + ViewModel.
// Показывает актуальный список favorite-продуктов текущего пользователя из Supabase/fallback источника.

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.sneakneak.di.AppContainer
import com.example.sneakneak.domain.favorites.model.FavoritesResult
import com.example.sneakneak.domain.favorites.usecase.FavoritesUseCases
import com.example.sneakneak.ui.components.AppIconAsset
import com.example.sneakneak.ui.components.InfoDialog
import com.example.sneakneak.ui.main.common.CatalogProductUiModel
import com.example.sneakneak.ui.main.common.FavoriteEmptyState
import com.example.sneakneak.ui.main.common.MainShellScaffold
import com.example.sneakneak.ui.main.common.MainTopBarStyle
import com.example.sneakneak.ui.main.common.ProductsGrid
import com.example.sneakneak.ui.main.common.toCatalogProductUiModel
import com.example.sneakneak.ui.navigation.AppRoutes
import com.example.sneakneak.ui.theme.AppColors
import com.example.sneakneak.ui.theme.AppTheme
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class FavoriteUiState(
    val title: String = "Избранное",
    val products: List<CatalogProductUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val dialogMessage: String? = null,
)

sealed interface FavoriteUiEvent {
    data class FavoriteToggled(val productId: String) : FavoriteUiEvent
    data object RetryClicked : FavoriteUiEvent
    data object BackClicked : FavoriteUiEvent
    data object DialogDismissed : FavoriteUiEvent
}

sealed interface FavoriteUiEffect {
    data object NavigateBack : FavoriteUiEffect
}

class FavoriteViewModel(
    private val useCases: FavoritesUseCases,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)

    var uiState by mutableStateOf(FavoriteUiState())
        private set

    var uiEffect by mutableStateOf<FavoriteUiEffect?>(null)
        private set

    init {
        observeFavoriteIds()
        loadFavorites()
    }

    fun onEvent(event: FavoriteUiEvent) {
        when (event) {
            is FavoriteUiEvent.FavoriteToggled -> toggleFavorite(event.productId)
            FavoriteUiEvent.RetryClicked -> loadFavorites()
            FavoriteUiEvent.BackClicked -> uiEffect = FavoriteUiEffect.NavigateBack
            FavoriteUiEvent.DialogDismissed -> uiState = uiState.copy(dialogMessage = null)
        }
    }

    fun consumeEffect() {
        uiEffect = null
    }

    private fun observeFavoriteIds() {
        scope.launch {
            // После toggle на другом экране просто перечитываем список без лишнего ручного refresh.
            useCases.observeFavoriteIds().collectLatest {
                loadFavorites(refreshIds = false)
            }
        }
    }

    private fun loadFavorites(refreshIds: Boolean = true) {
        scope.launch {
            uiState = uiState.copy(isLoading = true, dialogMessage = null)
            if (refreshIds) {
                when (val refresh = useCases.refreshFavoriteIds()) {
                    is FavoritesResult.Error -> {
                        uiState = uiState.copy(isLoading = false, dialogMessage = refresh.message)
                        return@launch
                    }

                    is FavoritesResult.Success -> Unit
                }
            }

            when (val result = useCases.getMyFavoriteProducts()) {
                is FavoritesResult.Error -> {
                    uiState = uiState.copy(isLoading = false, dialogMessage = result.message)
                }

                is FavoritesResult.Success -> {
                    uiState = uiState.copy(
                        products = result.data.map { it.toCatalogProductUiModel(isFavorite = true) },
                        isLoading = false,
                        dialogMessage = null,
                    )
                }
            }
        }
    }

    private fun toggleFavorite(productId: String) {
        scope.launch {
            when (val result = useCases.toggleFavorite(productId)) {
                is FavoritesResult.Error -> {
                    uiState = uiState.copy(dialogMessage = result.message)
                }

                is FavoritesResult.Success -> Unit
            }
        }
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}

@Composable
fun FavoriteRoute(
    currentRoute: String,
    onBottomNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: FavoriteViewModel = remember {
        FavoriteViewModel(AppContainer.favoritesUseCases)
    },
) {
    val effect = viewModel.uiEffect
    LaunchedEffect(effect) {
        when (effect) {
            FavoriteUiEffect.NavigateBack -> {
                onBack()
                viewModel.consumeEffect()
            }

            null -> Unit
        }
    }

    FavoriteScreen(
        state = viewModel.uiState,
        currentRoute = currentRoute,
        onEvent = viewModel::onEvent,
        onBottomNavigate = onBottomNavigate,
    )
}

@Composable
fun FavoriteScreen(
    state: FavoriteUiState,
    currentRoute: String,
    onEvent: (FavoriteUiEvent) -> Unit,
    onBottomNavigate: (String) -> Unit,
) {
    MainShellScaffold(
        currentRoute = currentRoute,
        topBarStyle = MainTopBarStyle.BackTitleAction(
            title = state.title,
            actionIcon = AppIconAsset.HeartFilled,
            actionTint = AppColors.Accent,
        ),
        onBottomItemClick = { onBottomNavigate(it.route) },
        onDrawerItemClick = {},
        onBackClick = {
            onEvent(FavoriteUiEvent.BackClicked)
        },
    ) { modifier ->
        Box(modifier = modifier) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                if (state.products.isEmpty() && !state.isLoading) {
                    FavoriteEmptyState()
                } else {
                    ProductsGrid(
                        products = state.products,
                        onFavoriteClick = { onEvent(FavoriteUiEvent.FavoriteToggled(it)) },
                    )
                }
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
            onDismiss = { onEvent(FavoriteUiEvent.DialogDismissed) },
        )
    }
}

private val favoritePreviewState = FavoriteUiState(
    products = listOf(
        CatalogProductUiModel(
            id = "p1",
            title = "PUMA Velophasis Phased",
            price = "16 999 ₽",
            imageUrl = null,
            isBestSeller = false,
            categoryId = "cat_1",
            isFavorite = true,
        ),
    ),
    isLoading = false,
)

@Preview
@Composable
private fun FavoritePreview() {
    AppTheme {
        FavoriteScreen(
            state = favoritePreviewState,
            currentRoute = AppRoutes.Favorite.route,
            onEvent = {},
            onBottomNavigate = {},
        )
    }
}
