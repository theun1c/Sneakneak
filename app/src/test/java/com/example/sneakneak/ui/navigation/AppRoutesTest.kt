package com.example.sneakneak.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class AppRoutesTest {

    @Test
    fun auth_routes_match_specification() {
        assertEquals("splash", AppRoutes.Splash.route)
        assertEquals("auth/signin", AppRoutes.SignIn.route)
        assertEquals("auth/signup", AppRoutes.SignUp.route)
        assertEquals("auth/forgot", AppRoutes.ForgotPassword.route)
        assertEquals("auth/otp?email={email}", AppRoutes.Otp.route)
        assertEquals("auth/new-password?email={email}", AppRoutes.NewPassword.route)
    }

    @Test
    fun main_routes_match_specification() {
        assertEquals("main/home", AppRoutes.Home.route)
        assertEquals("main/catalog", AppRoutes.Catalog.route)
        assertEquals("main/favorite", AppRoutes.Favorite.route)
        assertEquals("main/profile", AppRoutes.Profile.route)
        assertEquals("main/edit-profile", AppRoutes.EditProfile.route)
        assertEquals("main/loyalty-card", AppRoutes.LoyaltyCard.route)
        assertEquals("main/logout", AppRoutes.Logout.route)
    }
}
