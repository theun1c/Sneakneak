package com.example.sneakneak.ui.main.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.sneakneak.ui.components.CategoryChip
import com.example.sneakneak.ui.main.common.MockCatalogState
import com.example.sneakneak.ui.main.common.MainShellScaffold
import com.example.sneakneak.ui.main.common.MainTopBarStyle
import com.example.sneakneak.ui.main.common.ProductSectionHeader
import com.example.sneakneak.ui.main.common.ProductsGrid
import com.example.sneakneak.ui.navigation.AppRoutes
import com.example.sneakneak.ui.theme.AppTheme

// Catalog screen already has screen state and reusable product rendering,
// but filtering/favorites are still backed by the temporary mock catalog singleton.
data class CatalogUiState(
    val title: String = "Outdoor",
    val categories: List<String> = MockCatalogState.categories,
    val selectedCategory: String = "Outdoor",
)

sealed interface CatalogUiEvent {
    data class CategorySelected(val value: String) : CatalogUiEvent
    data object BackClicked : CatalogUiEvent
}

class CatalogViewModel : ViewModel() {
    var uiState by mutableStateOf(CatalogUiState())
        private set

    fun onEvent(event: CatalogUiEvent) {
        when (event) {
            is CatalogUiEvent.CategorySelected -> uiState = uiState.copy(
                selectedCategory = event.value,
                title = if (event.value == "Все") "Каталог" else event.value,
            )

            CatalogUiEvent.BackClicked -> Unit
        }
    }
}

@Composable
fun CatalogRoute(
    currentRoute: String,
    onBottomNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: CatalogViewModel = remember { CatalogViewModel() },
) {
    CatalogScreen(
        state = viewModel.uiState,
        currentRoute = currentRoute,
        onEvent = viewModel::onEvent,
        onBottomNavigate = onBottomNavigate,
        onBack = onBack,
    )
}

@Composable
fun CatalogScreen(
    state: CatalogUiState,
    currentRoute: String,
    onEvent: (CatalogUiEvent) -> Unit,
    onBottomNavigate: (String) -> Unit,
    onBack: () -> Unit,
) {
    MainShellScaffold(
        currentRoute = currentRoute,
        topBarStyle = MainTopBarStyle.BackTitleAction(title = state.title),
        onBottomItemClick = { onBottomNavigate(it.route) },
        onDrawerItemClick = {},
        onBackClick = onBack,
    ) { modifier ->
        Column(modifier = modifier.padding(horizontal = 20.dp)) {
            ProductSectionHeader(title = "Категории")
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.categories) { category ->
                    CategoryChip(
                        text = category,
                        selected = state.selectedCategory == category,
                        onClick = { onEvent(CatalogUiEvent.CategorySelected(category)) },
                    )
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            ProductsGrid(
                // TODO(DATA): bind grid content to repository-driven catalog state.
                products = MockCatalogState.productsFor(state.selectedCategory),
                onFavoriteClick = MockCatalogState::toggleFavorite,
            )
        }
    }
}

private val catalogPreviewState = CatalogUiState(
    title = "Running",
    selectedCategory = "Running",
)

@Preview
@Composable
private fun CatalogPreview() {
    AppTheme {
        CatalogScreen(
            state = catalogPreviewState,
            currentRoute = AppRoutes.Catalog.route,
            onEvent = {},
            onBottomNavigate = {},
            onBack = {},
        )
    }
}
