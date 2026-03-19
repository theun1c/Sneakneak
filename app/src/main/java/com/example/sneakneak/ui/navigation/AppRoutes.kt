package com.example.sneakneak.ui.navigation

// Единый реестр маршрутов приложения.
// Используется и auth-графом, и main-графом для консистентной навигации.
sealed class AppRoutes(val route: String) {
    data object Splash : AppRoutes("splash")
    data object SignIn : AppRoutes("auth/signin")
    data object SignUp : AppRoutes("auth/signup")
    data object ForgotPassword : AppRoutes("auth/forgot")
    data object Otp : AppRoutes("auth/otp?email={email}")
    data object NewPassword : AppRoutes("auth/new-password?email={email}")
    data object Home : AppRoutes("main/home")
    data object Catalog : AppRoutes("main/catalog")
    data object Favorite : AppRoutes("main/favorite")
    data object Notifications : AppRoutes("main/notifications")
    data object Profile : AppRoutes("main/profile")
    data object EditProfile : AppRoutes("main/edit-profile")
    data object LoyaltyCard : AppRoutes("main/loyalty-card")
    data object Logout : AppRoutes("main/logout")
    data object Cart : AppRoutes("main/cart")
    data object Orders : AppRoutes("main/orders")
    data object Settings : AppRoutes("main/settings")

    companion object {
        fun otp(email: String = ""): String = "auth/otp?email=$email"

        fun newPassword(email: String = ""): String = "auth/new-password?email=$email"
    }
}
