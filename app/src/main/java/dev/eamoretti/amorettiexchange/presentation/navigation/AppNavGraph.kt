package dev.eamoretti.amorettiexchange.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.eamoretti.amorettiexchange.presentation.auth.LoginScreen
import dev.eamoretti.amorettiexchange.presentation.clients.RegisterClientScreen
import dev.eamoretti.amorettiexchange.presentation.clients.model.Client
import dev.eamoretti.amorettiexchange.presentation.home.HomeScreen
import dev.eamoretti.amorettiexchange.presentation.transactions.RegisterTransactionScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    // This state is now hoisted to the NavGraph, making it the single source of truth.
    var currentScreen: AppScreen by remember { mutableStateOf(AppScreen.Clients) }

    val clients = listOf(
        Client("INVERSIONES Y NEGOCIOS CORPORATIVOS G.P.", "20611921701", "959224270"),
        Client("INMOBILIARIA VALEISA SAC", "20510510449", "912567088"),
        Client("BARRERA BENAVIDES JUANA MARIA LUISA", "08208278", "908708558")
    )

    NavHost(
        navController = navController,
        startDestination = AppScreen.Login.route
    ) {
        composable(AppScreen.Login.route) {
            LoginScreen(
                onAdminLogin = { navController.navigate(AppScreen.Home.route) { popUpTo(AppScreen.Login.route) { inclusive = true } } },
                onEmployeeLogin = { navController.navigate(AppScreen.Home.route) { popUpTo(AppScreen.Login.route) { inclusive = true } } }
            )
        }
        composable(AppScreen.Home.route) {
            HomeScreen(
                currentScreen = currentScreen,
                onScreenChange = { newScreen -> currentScreen = newScreen },
                onLogout = { navController.navigate(AppScreen.Login.route) { popUpTo(AppScreen.Home.route) { inclusive = true } } },
                onNavigateToRegisterClient = { navController.navigate(AppScreen.RegisterClient.route) },
                onNavigateToRegisterTransaction = {
                    // Before navigating, ensure the app knows we are in the "Transactions" context
                    currentScreen = AppScreen.Transactions
                    navController.navigate(AppScreen.RegisterTransaction.route)
                }
            )
        }
        composable(AppScreen.RegisterClient.route) {
            RegisterClientScreen(onNavigateBack = { navController.popBackStack() })
        }
        composable(AppScreen.RegisterTransaction.route) {
            // Now that state is managed correctly, popBackStack is safe to use.
            RegisterTransactionScreen(
                onNavigateBack = { navController.popBackStack() },
                clients = clients
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
    object RegisterTransaction : AppScreen("register_transaction")
}