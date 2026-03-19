package com.example.sneakneak.ui.screens

// Legacy-роут-обертка splash для связки с NavController.

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.sneakneak.ui.auth.splash.SplashRoute
import com.example.sneakneak.ui.navigation.AppRoutes
import com.example.sneakneak.ui.navigation.mockReplace

@Composable
fun SplashScreen(navController: NavController) {
    SplashRoute(
        onNavigateToSignIn = { navController.mockReplace(AppRoutes.SignIn.route) },
        onNavigateToHome = { navController.mockReplace(AppRoutes.Home.route) },
    )
}
