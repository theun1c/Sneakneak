package com.example.sneakneak.ui.main.catalog

// Экран Catalog + ViewModel.
// Отрисовывает read-only каталог и синхронизирует favorite state через общий поток избранного.

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.sneakneak.domain.products.model.Category
import com.example.sneakneak.domain.products.model.ProductsResult
import com.example.sneakneak.domain.products.usecase.ProductsUseCases
import com.example.sneakneak.ui.components.CategoryChip
import com.example.sneakneak.ui.components.InfoDialog
import com.example.sneakneak.ui.main.common.CatalogProductUiModel
import com.example.sneakneak.ui.main.common.MainShellScaffold
import com.example.sneakneak.ui.main.common.MainTopBarStyle
import com.example.sneakneak.ui.main.common.ProductSectionHeader
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

data class CatalogCategoryUiModel(
    val id: String,
    val title: String,
)

data class CatalogUiState(
    val title: String = "Каталог",
    val categories: List<CatalogCategoryUiModel> = listOf(CatalogCategoryUiModel(ALL_CATEGORY_ID, "Все")),
    val selectedCategoryId: String = ALL_CATEGORY_ID,
    val products: List<CatalogProductUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val dialogMessage: String? = null,
)

sealed interface CatalogUiEvent {
    data class CategorySelected(val categoryId: String) : CatalogUiEvent
    data class FavoriteToggled(val productId: String) : CatalogUiEvent
    data object RetryClicked : CatalogUiEvent
    data object BackClicked : CatalogUiEvent
    data object DialogDismissed : CatalogUiEvent
}

sealed interface CatalogUiEffect {
    data object NavigateBack : CatalogUiEffect
}

class CatalogViewModel(
    private val useCases: ProductsUseCases,
    private val favoritesUseCases: FavoritesUseCases,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private var allProducts: List<CatalogProductUiModel> = emptyList()
    private var selectedFavoriteIds = mutableSetOf<String>()

    var uiState by mutableStateOf(CatalogUiState())
        private set

    var uiEffect by mutableStateOf<CatalogUiEffect?>(null)
        private set

    init {
        observeFavoriteIds()
        loadContent()
    }

    fun onEvent(event: CatalogUiEvent) {
        when (event) {
            is CatalogUiEvent.CategorySelected -> {
                uiState = uiState.copy(
                    selectedCategoryId = event.categoryId,
                    title = titleFor(event.categoryId, uiState.categories),
                )
                applyFilters()
            }

            is CatalogUiEvent.FavoriteToggled -> {
                toggleFavorite(event.productId)
            }

            CatalogUiEvent.RetryClicked -> loadContent()
            CatalogUiEvent.BackClicked -> uiEffect = CatalogUiEffect.NavigateBack
            CatalogUiEvent.DialogDismissed -> uiState = uiState.copy(dialogMessage = null)
        }
    }

    fun consumeEffect() {
        uiEffect = null
    }

    private fun loadContent() {
        scope.launch {
            uiState = uiState.copy(isLoading = true, dialogMessage = null)

            when (val favoritesResult = favoritesUseCases.refreshFavoriteIds()) {
                is FavoritesResult.Error -> {
                    uiState = uiState.copy(isLoading = false, dialogMessage = favoritesResult.message)
                    return@launch
                }

                is FavoritesResult.Success -> Unit
            }

            val categoriesResult = useCases.getCategories()
            val productsResult = useCases.getCatalogProducts()

            val categories = when (categoriesResult) {
                is ProductsResult.Error -> {
                    uiState = uiState.copy(isLoading = false, dialogMessage = categoriesResult.message)
                    return@launch
                }

                is ProductsResult.Success -> categoriesResult.data
            }

            val products = when (productsResult) {
                is ProductsResult.Error -> {
                    uiState = uiState.copy(isLoading = false, dialogMessage = productsResult.message)
                    return@launch
                }

                is ProductsResult.Success -> productsResult.data
            }

            allProducts = products.map { it.toCatalogProductUiModel(isFavorite = selectedFavoriteIds.contains(it.id)) }
            val categoryItems = buildCatalogCategories(categories)
            val selected = resolveSelectedCategoryId(
                current = uiState.selectedCategoryId,
                categories = categories,
            )

            uiState = uiState.copy(
                categories = categoryItems,
                selectedCategoryId = selected,
                title = titleFor(selected, categoryItems),
                isLoading = false,
                dialogMessage = null,
            )
            applyFilters()
        }
    }

    private fun applyFilters() {
        val filtered = allProducts
            .filter { product ->
                uiState.selectedCategoryId == ALL_CATEGORY_ID || product.categoryId == uiState.selectedCategoryId
            }
            .map { product ->
                product.copy(isFavorite = selectedFavoriteIds.contains(product.id))
            }

        uiState = uiState.copy(products = filtered)
    }

    private fun observeFavoriteIds() {
        scope.launch {
            // Подписка обеспечивает мгновенное обновление сердечка после изменений в Home/Favorite.
            favoritesUseCases.observeFavoriteIds().collectLatest { ids ->
                selectedFavoriteIds = ids.toMutableSet()
                applyFilters()
            }
        }
    }

    private fun toggleFavorite(productId: String) {
        scope.launch {
            when (val result = favoritesUseCases.toggleFavorite(productId)) {
                is FavoritesResult.Error -> {
                    uiState = uiState.copy(dialogMessage = result.message)
                }

                is FavoritesResult.Success -> Unit
            }
        }
    }

    private fun titleFor(
        categoryId: String,
        categories: List<CatalogCategoryUiModel>,
    ): String {
        if (categoryId == ALL_CATEGORY_ID) return "Каталог"
        return categories.firstOrNull { it.id == categoryId }?.title ?: "Каталог"
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}

@Composable
fun CatalogRoute(
    currentRoute: String,
    onBottomNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: CatalogViewModel = remember {
        CatalogViewModel(
            useCases = AppContainer.productsUseCases,
            favoritesUseCases = AppContainer.favoritesUseCases,
        )
    },
) {
    val effect = viewModel.uiEffect
    LaunchedEffect(effect) {
        when (effect) {
            CatalogUiEffect.NavigateBack -> {
                onBack()
                viewModel.consumeEffect()
            }

            null -> Unit
        }
    }

    CatalogScreen(
        state = viewModel.uiState,
        currentRoute = currentRoute,
        onEvent = viewModel::onEvent,
        onBottomNavigate = onBottomNavigate,
    )
}

@Composable
fun CatalogScreen(
    state: CatalogUiState,
    currentRoute: String,
    onEvent: (CatalogUiEvent) -> Unit,
    onBottomNavigate: (String) -> Unit,
) {
    MainShellScaffold(
        currentRoute = currentRoute,
        topBarStyle = MainTopBarStyle.BackTitleAction(title = state.title),
        onBottomItemClick = { onBottomNavigate(it.route) },
        onDrawerItemClick = {},
        onBackClick = {
            onEvent(CatalogUiEvent.BackClicked)
        },
    ) { modifier ->
        Box(modifier = modifier) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                ProductSectionHeader(title = "Категории")
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.categories, key = { it.id }) { category ->
                        CategoryChip(
                            text = category.title,
                            selected = state.selectedCategoryId == category.id,
                            onClick = { onEvent(CatalogUiEvent.CategorySelected(category.id)) },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(18.dp))
                if (state.products.isEmpty() && !state.isLoading) {
                    Text(
                        text = "Товары пока не найдены",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppColors.TextSecondary,
                    )
                } else {
                    ProductsGrid(
                        products = state.products,
                        onFavoriteClick = { onEvent(CatalogUiEvent.FavoriteToggled(it)) },
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
            onDismiss = { onEvent(CatalogUiEvent.DialogDismissed) },
        )
    }
}

private fun buildCatalogCategories(categories: List<Category>): List<CatalogCategoryUiModel> {
    return listOf(CatalogCategoryUiModel(ALL_CATEGORY_ID, "Все")) + categories.map {
        CatalogCategoryUiModel(id = it.id, title = it.title)
    }
}

private fun resolveSelectedCategoryId(
    current: String,
    categories: List<Category>,
): String {
    if (current == ALL_CATEGORY_ID) return current
    return if (categories.any { it.id == current }) current else ALL_CATEGORY_ID
}

private const val ALL_CATEGORY_ID = "__all__"

private val catalogPreviewState = CatalogUiState(
    title = "Каталог",
    categories = listOf(
        CatalogCategoryUiModel(ALL_CATEGORY_ID, "Все"),
        CatalogCategoryUiModel("c1", "Outdoor"),
        CatalogCategoryUiModel("c2", "Tennis"),
    ),
    selectedCategoryId = ALL_CATEGORY_ID,
    products = listOf(
        CatalogProductUiModel(
            id = "1",
            title = "PUMA Velophasis Phased",
            price = "16 999 ₽",
            imageUrl = null,
            isBestSeller = false,
            categoryId = "c2",
            isFavorite = false,
        ),
        CatalogProductUiModel(
            id = "2",
            title = "Adidas Ozmillen",
            price = "20 599 ₽",
            imageUrl = null,
            isBestSeller = true,
            categoryId = "c1",
            isFavorite = true,
        ),
    ),
    isLoading = false,
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
        )
    }
}
