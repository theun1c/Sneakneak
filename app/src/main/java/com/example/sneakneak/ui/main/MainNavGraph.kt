package com.example.sneakneak.ui.main

// Навигационный граф авторизованной части приложения (main shell).
// Внутри связываются Home/Catalog/Favorite/Profile/Loyalty и экран редактирования профиля.

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.sneakneak.di.AppContainer
import com.example.sneakneak.ui.main.catalog.CatalogRoute
import com.example.sneakneak.ui.main.favorite.FavoriteRoute
import com.example.sneakneak.ui.main.home.HomeRoute
import com.example.sneakneak.ui.main.loyalty.LoyaltyCardRoute
import com.example.sneakneak.ui.main.common.MainShellScaffold
import com.example.sneakneak.ui.main.common.MainTopBarStyle
import com.example.sneakneak.ui.main.profile.EditProfileRoute
import com.example.sneakneak.ui.main.profile.ProfileRoute
import com.example.sneakneak.ui.navigation.AppRoutes
import com.example.sneakneak.ui.navigation.mockNavigate
import com.example.sneakneak.ui.navigation.mockReplace
import kotlinx.coroutines.launch

// Main graph hosts the post-auth shell.
// Home/Catalog/Profile now resolve data through repositories; remaining placeholders stay isolated.
fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController,
) {
    navigation(
        route = "main",
        startDestination = AppRoutes.Home.route,
    ) {
        composable(AppRoutes.Home.route) {
            val drawerNavigate = rememberMainDrawerNavigate(navController)
            HomeRoute(
                currentRoute = AppRoutes.Home.route,
                onBottomNavigate = navController::mockNavigate,
                onDrawerNavigate = drawerNavigate,
                onTopActionClick = { navController.mockNavigate(AppRoutes.Catalog.route) },
            )
        }
        composable(AppRoutes.Catalog.route) {
            CatalogRoute(
                currentRoute = AppRoutes.Catalog.route,
                onBottomNavigate = navController::mockNavigate,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppRoutes.Favorite.route) {
            FavoriteRoute(
                currentRoute = AppRoutes.Favorite.route,
                onBottomNavigate = navController::mockNavigate,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppRoutes.Profile.route) {
            val drawerNavigate = rememberMainDrawerNavigate(navController)
            ProfileRoute(
                currentRoute = AppRoutes.Profile.route,
                onBottomNavigate = navController::mockNavigate,
                onDrawerNavigate = drawerNavigate,
                onEditClick = { navController.mockNavigate(AppRoutes.EditProfile.route) },
                onBarcodeClick = { navController.mockNavigate(AppRoutes.LoyaltyCard.route) },
            )
        }
        composable(AppRoutes.EditProfile.route) {
            EditProfileRoute(
                currentRoute = AppRoutes.Profile.route,
                onBottomNavigate = navController::mockNavigate,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppRoutes.LoyaltyCard.route) {
            LoyaltyCardRoute(
                currentRoute = AppRoutes.LoyaltyCard.route,
                onBottomNavigate = navController::mockNavigate,
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppRoutes.Notifications.route) {
            MainPlaceholderRoute(
                currentRoute = AppRoutes.Notifications.route,
                title = "Уведомления",
                navController = navController,
            )
        }
        composable(AppRoutes.Cart.route) {
            MainPlaceholderRoute(
                currentRoute = AppRoutes.Cart.route,
                title = "Корзина",
                navController = navController,
            )
        }
        composable(AppRoutes.Orders.route) {
            MainPlaceholderRoute(
                currentRoute = AppRoutes.Orders.route,
                title = "Заказы",
                navController = navController,
            )
        }
        composable(AppRoutes.Settings.route) {
            MainPlaceholderRoute(
                currentRoute = AppRoutes.Settings.route,
                title = "Настройки",
                navController = navController,
            )
        }
    }
}

@Composable
private fun MainPlaceholderRoute(
    currentRoute: String,
    title: String,
    navController: NavHostController,
) {
    val drawerNavigate = rememberMainDrawerNavigate(navController)

    MainShellScaffold(
        currentRoute = currentRoute,
        topBarStyle = MainTopBarStyle.BackTitleAction(title = title),
        onBottomItemClick = { navController.mockNavigate(it.route) },
        onDrawerItemClick = { drawerNavigate(it.route) },
        onBackClick = { navController.popBackStack() },
    ) { modifier ->
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
            )
        }
    }
}

@Composable
private fun rememberMainDrawerNavigate(
    navController: NavHostController,
): (String) -> Unit {
    val scope = rememberCoroutineScope()

    return remember(navController, scope) {
        { route ->
            if (route == AppRoutes.Logout.route) {
                scope.launch {
                    // Uses repository signOut (Supabase when configured, fake fallback otherwise).
                    AppContainer.authUseCases.signOut()
                    navController.mockReplace(AppRoutes.SignIn.route)
                }
            } else {
                navController.mockNavigate(route)
            }
        }
    }
}
