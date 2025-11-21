package dev.eamoretti.amorettiexchange.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.eamoretti.amorettiexchange.presentation.auth.LoginScreen
import dev.eamoretti.amorettiexchange.presentation.clients.RegisterClientScreen
import dev.eamoretti.amorettiexchange.presentation.home.HomeScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppScreen.Login.route
    ) {
        composable(AppScreen.Login.route) {
            LoginScreen(
                onAdminLogin = {
                    navController.navigate(AppScreen.Home.route) {
                        popUpTo(AppScreen.Login.route) { inclusive = true }
                    }
                },
                onEmployeeLogin = {
                    navController.navigate(AppScreen.Home.route) {
                        popUpTo(AppScreen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppScreen.Home.route) {
            HomeScreen(
                onLogout = {
                    navController.navigate(AppScreen.Login.route) {
                        popUpTo(AppScreen.Home.route) { inclusive = true }
                    }
                },
                onNavigateToRegisterClient = {
                    navController.navigate(AppScreen.RegisterClient.route)
                }
            )
        }
        composable(AppScreen.RegisterClient.route) {
            RegisterClientScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

sealed class AppScreen(val route: String) {
    object Login : AppScreen("login")
    object Home : AppScreen("home")
    object Clients : AppScreen("clients")
    object Transactions : AppScreen("transactions")
    object MonthlyBalancing : AppScreen("monthly_balancing")
    object RegisterClient : AppScreen("register_client")
}