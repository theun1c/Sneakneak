package com.example.sneakneak.ui.navigation

import androidx.navigation.NavController

// TODO(UI->DOMAIN): replace helper naming once navigation is fully driven by session/data results.
// For the UI stage these wrappers make intent explicit: stack push vs stack replace.
fun NavController.mockNavigate(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}

fun NavController.mockReplace(route: String) {
    navigate(route) {
        launchSingleTop = true
        popUpTo(graph.startDestinationId) {
            saveState = false
        }
    }
}
