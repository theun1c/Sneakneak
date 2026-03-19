package com.example.sneakneak.ui.main.home

// Экран Home + ViewModel.
// Источник данных: ProductsUseCases + FavoritesUseCases; UI получает уже готовые UiModel.

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import coil.compose.SubcomposeAsyncImage
import com.example.sneakneak.di.AppContainer
import com.example.sneakneak.domain.favorites.model.FavoritesResult
import com.example.sneakneak.domain.favorites.usecase.FavoritesUseCases
import com.example.sneakneak.domain.products.model.Category
import com.example.sneakneak.domain.products.model.ProductsResult
import com.example.sneakneak.domain.products.model.Promotion
import com.example.sneakneak.domain.products.usecase.ProductsUseCases
import com.example.sneakneak.ui.assets.DesignAssets
import com.example.sneakneak.ui.assets.DesignPngAsset
import com.example.sneakneak.ui.assets.HomeVisualTuning
import com.example.sneakneak.ui.components.CategoryChip
import com.example.sneakneak.ui.components.InfoDialog
import com.example.sneakneak.ui.components.ProductCard
import com.example.sneakneak.ui.components.SearchBar
import com.example.sneakneak.ui.main.common.CatalogProductUiModel
import com.example.sneakneak.ui.main.common.HomePromotionUiModel
import com.example.sneakneak.ui.main.common.MainShellScaffold
import com.example.sneakneak.ui.main.common.MainTopBarStyle
import com.example.sneakneak.ui.main.common.ProductSectionHeader
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

data class HomeCategoryUiModel(
    val id: String,
    val title: String,
)

data class HomeUiState(
    val query: String = "",
    val categories: List<HomeCategoryUiModel> = listOf(HomeCategoryUiModel(ALL_CATEGORY_ID, "Все")),
    val selectedCategoryId: String = ALL_CATEGORY_ID,
    val featuredProducts: List<CatalogProductUiModel> = emptyList(),
    val promotions: List<HomePromotionUiModel> = emptyList(),
    val isLoading: Boolean = true,
    val dialogMessage: String? = null,
)

sealed interface HomeUiEvent {
    data class QueryChanged(val value: String) : HomeUiEvent
    data class CategorySelected(val categoryId: String) : HomeUiEvent
    data class FavoriteToggled(val productId: String) : HomeUiEvent
    data object SeeAllPopularClicked : HomeUiEvent
    data object RetryClicked : HomeUiEvent
    data object DialogDismissed : HomeUiEvent
}

sealed interface HomeUiEffect {
    data object NavigateToCatalog : HomeUiEffect
}

class HomeViewModel(
    private val useCases: ProductsUseCases,
    private val favoritesUseCases: FavoritesUseCases,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private var bestSellerProducts: List<CatalogProductUiModel> = emptyList()
    private var selectedFavoriteIds = mutableSetOf<String>()

    var uiState by mutableStateOf(HomeUiState())
        private set

    var uiEffect by mutableStateOf<HomeUiEffect?>(null)
        private set

    init {
        observeFavoriteIds()
        loadContent()
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.QueryChanged -> {
                uiState = uiState.copy(query = event.value)
                applyFilters()
            }

            is HomeUiEvent.CategorySelected -> {
                uiState = uiState.copy(selectedCategoryId = event.categoryId)
                applyFilters()
            }

            is HomeUiEvent.FavoriteToggled -> {
                toggleFavorite(event.productId)
            }

            HomeUiEvent.SeeAllPopularClicked -> uiEffect = HomeUiEffect.NavigateToCatalog
            HomeUiEvent.RetryClicked -> loadContent()
            HomeUiEvent.DialogDismissed -> uiState = uiState.copy(dialogMessage = null)
        }
    }

    fun consumeEffect() {
        uiEffect = null
    }

    private fun loadContent() {
        scope.launch {
            // Загрузка выполняется последовательно, чтобы корректно показать первую ошибку пользователю в диалоге.
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
            val promotionsResult = useCases.getPromotions()

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

            val promotions = when (promotionsResult) {
                is ProductsResult.Error -> {
                    emptyList()
                }

                is ProductsResult.Success -> promotionsResult.data
            }

            bestSellerProducts = products.map { product ->
                product.toCatalogProductUiModel(isFavorite = selectedFavoriteIds.contains(product.id))
            }
            uiState = uiState.copy(
                categories = buildHomeCategories(categories),
                selectedCategoryId = resolveSelectedCategoryId(
                    current = uiState.selectedCategoryId,
                    categories = categories,
                ),
                promotions = promotions.map { it.toUiModel() },
                isLoading = false,
                dialogMessage = null,
            )
            applyFilters()
        }
    }

    private fun applyFilters() {
        val selectedCategoryId = uiState.selectedCategoryId
        val query = uiState.query.trim()

        val filtered = bestSellerProducts
            .filter { product ->
                selectedCategoryId == ALL_CATEGORY_ID || product.categoryId == selectedCategoryId
            }
            .filter { product ->
                if (query.isBlank()) {
                    true
                } else {
                    product.title.contains(query, ignoreCase = true)
                }
            }
            .map { product ->
                product.copy(isFavorite = selectedFavoriteIds.contains(product.id))
            }

        uiState = uiState.copy(featuredProducts = filtered)
    }

    private fun observeFavoriteIds() {
        scope.launch {
            // Реактивная синхронизация сердечек в карточках Home при изменении избранного в других экранах.
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

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}

@Composable
fun HomeRoute(
    currentRoute: String,
    onBottomNavigate: (String) -> Unit,
    onDrawerNavigate: (String) -> Unit,
    onTopActionClick: () -> Unit,
    viewModel: HomeViewModel = remember {
        HomeViewModel(
            useCases = AppContainer.productsUseCases,
            favoritesUseCases = AppContainer.favoritesUseCases,
        )
    },
) {
    val effect = viewModel.uiEffect
    LaunchedEffect(effect) {
        when (effect) {
            HomeUiEffect.NavigateToCatalog -> {
                onTopActionClick()
                viewModel.consumeEffect()
            }

            null -> Unit
        }
    }

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
        Box(modifier = modifier) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
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
                    items(state.categories, key = { it.id }) { category ->
                        CategoryChip(
                            text = category.title,
                            selected = state.selectedCategoryId == category.id,
                            onClick = { onEvent(HomeUiEvent.CategorySelected(category.id)) },
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                ProductSectionHeader(
                    title = "Популярное",
                    action = "Все",
                    onActionClick = { onEvent(HomeUiEvent.SeeAllPopularClicked) },
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (state.featuredProducts.isEmpty() && !state.isLoading) {
                    Text(
                        text = "Товары пока не найдены",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppColors.TextSecondary,
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        items(state.featuredProducts, key = { it.id }) { product ->
                            ProductCard(
                                title = product.title,
                                price = product.price,
                                imageUrl = product.imageUrl,
                                modifier = Modifier.fillParentMaxWidth(0.48f),
                                isBestSeller = product.isBestSeller,
                                isFavorite = product.isFavorite,
                                onFavoriteClick = { onEvent(HomeUiEvent.FavoriteToggled(product.id)) },
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                ProductSectionHeader(title = "Акции", action = "Все")
                Spacer(modifier = Modifier.height(12.dp))
                if (state.promotions.isEmpty()) {
                    DesignPngAsset(
                        assetPath = DesignAssets.Stock,
                        contentDescription = "Промо",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(HomeVisualTuning.promotionHeightDp.dp),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    PromotionBanner(
                        imageUrl = state.promotions.first().imageUrl,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(HomeVisualTuning.promotionHeightDp.dp),
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
            onDismiss = { onEvent(HomeUiEvent.DialogDismissed) },
        )
    }
}

@Composable
private fun PromotionBanner(
    imageUrl: String?,
    modifier: Modifier = Modifier,
) {
    if (imageUrl.isNullOrBlank()) {
        DesignPngAsset(
            assetPath = DesignAssets.Stock,
            contentDescription = "Промо",
            modifier = modifier,
            contentScale = ContentScale.Fit,
        )
        return
    }

    SubcomposeAsyncImage(
        model = imageUrl,
        contentDescription = "Промо",
        modifier = modifier,
        contentScale = ContentScale.Crop,
        loading = {
            DesignPngAsset(
                assetPath = DesignAssets.Stock,
                contentDescription = "Промо",
                modifier = modifier,
                contentScale = ContentScale.Fit,
            )
        },
        error = {
            DesignPngAsset(
                assetPath = DesignAssets.Stock,
                contentDescription = "Промо",
                modifier = modifier,
                contentScale = ContentScale.Fit,
            )
        },
    )
}

private fun buildHomeCategories(categories: List<Category>): List<HomeCategoryUiModel> {
    return listOf(HomeCategoryUiModel(ALL_CATEGORY_ID, "Все")) + categories.map {
        HomeCategoryUiModel(id = it.id, title = it.title)
    }
}

private fun resolveSelectedCategoryId(
    current: String,
    categories: List<Category>,
): String {
    if (current == ALL_CATEGORY_ID) return current
    return if (categories.any { it.id == current }) current else ALL_CATEGORY_ID
}

private fun Promotion.toUiModel(): HomePromotionUiModel {
    return HomePromotionUiModel(
        id = id,
        // TODO(SUPABASE): map `actions.photo` storage path to URL once promotions bucket contract is finalized.
        imageUrl = photo.takeIf { it!!.isNotBlank() },
    )
}

private const val ALL_CATEGORY_ID = "__all__"

private val homePreviewState = HomeUiState(
    query = "Puma",
    categories = listOf(
        HomeCategoryUiModel(ALL_CATEGORY_ID, "Все"),
        HomeCategoryUiModel("c1", "Outdoor"),
        HomeCategoryUiModel("c2", "Tennis"),
    ),
    selectedCategoryId = ALL_CATEGORY_ID,
    featuredProducts = listOf(
        CatalogProductUiModel(
            id = "1",
            title = "PUMA Velophasis Phased",
            price = "16 999 ₽",
            imageUrl = null,
            isBestSeller = false,
            categoryId = "c2",
        ),
        CatalogProductUiModel(
            id = "2",
            title = "Adidas Ozmillen",
            price = "20 599 ₽",
            imageUrl = null,
            isBestSeller = true,
            categoryId = "c1",
        ),
    ),
    promotions = listOf(HomePromotionUiModel("promo_1", null)),
    isLoading = false,
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
