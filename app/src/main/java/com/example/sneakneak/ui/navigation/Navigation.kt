package com.example.sneakneak.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sneakneak.ui.auth.forgot.ForgotPasswordRoute
import com.example.sneakneak.ui.auth.newpassword.NewPasswordRoute
import com.example.sneakneak.ui.auth.otp.OtpRoute
import com.example.sneakneak.ui.auth.signin.SignInRoute
import com.example.sneakneak.ui.auth.signup.SignUpRoute
import com.example.sneakneak.ui.auth.splash.SplashRoute
import com.example.sneakneak.ui.main.mainNavGraph

// Root navigation host for the current UI stage.
// Auth and main flows are already split, while transitions still run on mock session state.
@Composable
fun Navigation() = AppNavHost()

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AppRoutes.Splash.route) {
        composable(AppRoutes.Splash.route) {
            // TODO(DATA): route selection should stay here, but the source of truth must become
            // a real session observer once Supabase auth is connected.
            SplashRoute(
                onNavigateToSignIn = { navController.mockReplace(AppRoutes.SignIn.route) },
                onNavigateToHome = { navController.mockReplace(AppRoutes.Home.route) },
            )
        }
        composable(AppRoutes.SignIn.route) {
            SignInRoute(
                onForgotPassword = { navController.mockNavigate(AppRoutes.ForgotPassword.route) },
                onCreateAccount = { navController.mockNavigate(AppRoutes.SignUp.route) },
                onSignInSuccess = { navController.mockReplace(AppRoutes.Home.route) },
            )
        }
        composable(AppRoutes.SignUp.route) {
            SignUpRoute(
                onBack = { navController.popBackStack() },
                onNavigateToSignIn = { navController.popBackStack(AppRoutes.SignIn.route, false) },
            )
        }
        composable(AppRoutes.ForgotPassword.route) {
            ForgotPasswordRoute(
                onBack = { navController.popBackStack() },
                onNavigateToOtp = { email -> navController.mockNavigate(AppRoutes.otp(email)) },
            )
        }
        composable(
            route = AppRoutes.Otp.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) { backStackEntry ->
            OtpRoute(
                email = backStackEntry.arguments?.getString("email").orEmpty(),
                onBack = { navController.popBackStack() },
                onNavigateToNewPassword = { email -> navController.mockNavigate(AppRoutes.newPassword(email)) },
            )
        }
        composable(
            route = AppRoutes.NewPassword.route,
            arguments = listOf(
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = ""
                },
            ),
        ) {
            NewPasswordRoute(
                email = it.arguments?.getString("email").orEmpty(),
                onBack = { navController.popBackStack() },
                onNavigateToSignIn = { navController.mockReplace(AppRoutes.SignIn.route) },
            )
        }
        mainNavGraph(navController)
    }
}
