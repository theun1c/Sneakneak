package com.example.sneakneak.ui.screens

// Legacy заглушка экрана main из раннего UI-этапа.
// Оставлена для совместимости, основной поток сейчас идет через ui/main/MainNavGraph.

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun MainScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(text = "Main Screen",
            color = Color.Black,
            fontSize = 24.sp
        )
    }
}
