package com.example.sneakneak.ui.main.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.example.sneakneak.ui.assets.DesignAssets
import com.example.sneakneak.ui.assets.DesignPngAsset
import com.example.sneakneak.ui.assets.HomeVisualTuning
import com.example.sneakneak.ui.components.CategoryChip
import com.example.sneakneak.ui.components.SearchBar
import com.example.sneakneak.ui.main.common.MockCatalogState
import com.example.sneakneak.ui.main.common.MainShellScaffold
import com.example.sneakneak.ui.main.common.MainTopBarStyle
import com.example.sneakneak.ui.main.common.ProductPairRow
import com.example.sneakneak.ui.main.common.ProductSectionHeader
import com.example.sneakneak.ui.navigation.AppRoutes
import com.example.sneakneak.ui.theme.AppTheme

// Home screen is visually complete, but its content still comes from local mock state/assets.
data class HomeUiState(
    val query: String = "",
    val categories: List<String> = MockCatalogState.categories,
    val selectedCategory: String = "Все",
    val featuredProducts: List<com.example.sneakneak.ui.main.common.MockProduct> = MockCatalogState.products.take(2),
)

sealed interface HomeUiEvent {
    data class QueryChanged(val value: String) : HomeUiEvent
    data class CategorySelected(val value: String) : HomeUiEvent
    data object MenuClicked : HomeUiEvent
    data object CartClicked : HomeUiEvent
}

class HomeViewModel : ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.QueryChanged -> uiState = uiState.copy(query = event.value)
            is HomeUiEvent.CategorySelected -> uiState = uiState.copy(
                selectedCategory = event.value,
                // TODO(DATA): replace MockCatalogState with products/categories use cases.
                featuredProducts = MockCatalogState.productsFor(event.value).take(2),
            )
            HomeUiEvent.MenuClicked, HomeUiEvent.CartClicked -> Unit
        }
    }
}

@Composable
fun HomeRoute(
    currentRoute: String,
    onBottomNavigate: (String) -> Unit,
    onDrawerNavigate: (String) -> Unit,
    onTopActionClick: () -> Unit,
    viewModel: HomeViewModel = remember { HomeViewModel() },
) {
    HomeScreen(
        state = viewModel.uiState,
        currentRoute = currentRoute,
        onEvent = viewModel::onEvent,
        onBottomNavigate = onBottomNavigate,
        onDrawerNavigate = onDrawerNavigate,
        onTopActionClick = onTopActionClick,
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    currentRoute: String,
    onEvent: (HomeUiEvent) -> Unit,
    onBottomNavigate: (String) -> Unit,
    onDrawerNavigate: (String) -> Unit,
    onTopActionClick: () -> Unit,
) {
    MainShellScaffold(
        currentRoute = currentRoute,
        topBarStyle = MainTopBarStyle.MenuTitleAction(
            title = "Главная",
            actionIcon = com.example.sneakneak.ui.components.AppIconAsset.Bag,
            actionBadge = true,
        ),
        onBottomItemClick = { onBottomNavigate(it.route) },
        onDrawerItemClick = { onDrawerNavigate(it.route) },
        onTopActionClick = onTopActionClick,
    ) { modifier ->
        Column(
            modifier = modifier.padding(horizontal = 20.dp),
        ) {
            SearchBar(
                query = state.query,
                onQueryChange = { onEvent(HomeUiEvent.QueryChanged(it)) },
                onFilterClick = {},
            )
            Spacer(modifier = Modifier.height(22.dp))
            ProductSectionHeader(title = "Категории")
            Spacer(modifier = Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.categories) { category ->
                    CategoryChip(
                        text = category,
                        selected = state.selectedCategory == category,
                        onClick = { onEvent(HomeUiEvent.CategorySelected(category)) },
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            ProductSectionHeader(title = "Популярное", action = "Все")
            Spacer(modifier = Modifier.height(12.dp))
            ProductPairRow(
                products = state.featuredProducts,
                onFavoriteClick = MockCatalogState::toggleFavorite,
            )
            Spacer(modifier = Modifier.height(24.dp))
            ProductSectionHeader(title = "Акции", action = "Все")
            Spacer(modifier = Modifier.height(12.dp))
            DesignPngAsset(
                assetPath = DesignAssets.Stock,
                contentDescription = "Промо",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HomeVisualTuning.promotionHeightDp.dp),
                contentScale = androidx.compose.ui.layout.ContentScale.Fit,
            )
        }
    }
}

private val homePreviewState = HomeUiState(
    query = "Nike",
    selectedCategory = "Outdoor",
    featuredProducts = MockCatalogState.productsFor("Outdoor").take(2),
)

@Preview
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(
            state = homePreviewState,
            currentRoute = AppRoutes.Home.route,
            onEvent = {},
            onBottomNavigate = {},
            onDrawerNavigate = {},
            onTopActionClick = {},
        )
    }
}
