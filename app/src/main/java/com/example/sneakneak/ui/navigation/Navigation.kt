package com.example.sneakneak.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sneakneak.ui.screens.MainScreen
import com.example.sneakneak.ui.screens.RegisterScreen
import com.example.sneakneak.ui.screens.SplashScreen

@Composable
fun Navigation(){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash_screen") {
        // Splash screen
        composable("splash_screen") {
            SplashScreen(navController = navController)
        }

        // Main screen
        composable("main_screen") {
            MainScreen(navController = navController)
        }

        // Register screen
        composable("register_screen"){
            RegisterScreen()
        }
    }
}