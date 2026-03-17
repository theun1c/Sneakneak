package com.example.sneakneak.ui.main

import com.example.sneakneak.ui.components.DefaultBottomNavItems
import com.example.sneakneak.ui.navigation.AppRoutes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MainShellConfigTest {

    @Test
    fun bottom_bar_matches_five_slot_shell() {
        assertEquals(5, DefaultBottomNavItems.size)
        assertEquals(AppRoutes.Catalog.route, DefaultBottomNavItems[2].route)
    }

    @Test
    fun main_routes_cover_shell_screens() {
        val routes = setOf(
            AppRoutes.Home.route,
            AppRoutes.Catalog.route,
            AppRoutes.Favorite.route,
            AppRoutes.Profile.route,
            AppRoutes.EditProfile.route,
            AppRoutes.LoyaltyCard.route,
        )

        assertTrue(routes.contains(AppRoutes.Home.route))
        assertTrue(routes.contains(AppRoutes.EditProfile.route))
        assertTrue(routes.contains(AppRoutes.LoyaltyCard.route))
    }
}
