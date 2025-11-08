package com.example.proyectofinal.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.data.model.UserRole
import com.example.proyectofinal.ui.screens.AdminScreen
import com.example.proyectofinal.ui.screens.HomeScreen
import com.example.proyectofinal.ui.screens.LoginScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Admin : Screen("admin")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var currentUserRole by remember { mutableStateOf<UserRole?>(null) }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // Pantalla de Login
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { role ->
                    currentUserRole = role
                    when (role) {
                        UserRole.USER -> navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                        UserRole.ADMIN -> navController.navigate(Screen.Admin.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        // Pantalla de Usuario Normal (Películas)
        composable(Screen.Home.route) {
            HomeScreen(
                onLogout = {
                    currentUserRole = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de Admin (Sensores)
        composable(Screen.Admin.route) {
            AdminScreen(
                onLogout = {
                    currentUserRole = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}