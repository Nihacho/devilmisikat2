package com.example.proyectofinal.ui.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectofinal.data.model.UserRole
import com.example.proyectofinal.ui.screens.AdminScreen
import com.example.proyectofinal.ui.screens.LoginScreen
import com.example.proyectofinal.ui.screens.MainScreen
import com.example.proyectofinal.ui.screens.MovieDetailScreen
import com.example.proyectofinal.ui.viewmodel.MoviesViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home") // This is now the container for the main screens
    object Admin : Screen("admin")
    object MovieDetail : Screen("movie_detail/{movieId}") {
        fun createRoute(movieId: String) = "movie_detail/$movieId"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    var currentUserRole by remember { mutableStateOf<UserRole?>(null) }
    
    // Shared ViewModel
    val moviesViewModel: MoviesViewModel = viewModel()

    Box(modifier = androidx.compose.ui.Modifier.fillMaxSize()) {
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

            // Pantalla Principal (con Bottom Navigation)
            composable(Screen.Home.route) {
                MainScreen(
                    viewModel = moviesViewModel,
                    isDarkTheme = isDarkTheme,
                    onThemeChanged = onThemeChanged,
                    onLogout = {
                        currentUserRole = null
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onMovieClick = { movie ->
                        // navigate to detail or play directly? Detail for now
                        navController.navigate(Screen.MovieDetail.createRoute(movie.id))
                    }
                )
            }

            // Pantalla de Detalle de Película
            composable(
                route = Screen.MovieDetail.route,
                arguments = listOf(
                    navArgument("movieId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getString("movieId") ?: ""
                MovieDetailScreen(
                    viewModel = moviesViewModel,
                    movieId = movieId,
                    onBack = {
                        navController.popBackStack()
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
        
        // Reproductor de Video (Global Overlay)
        com.example.proyectofinal.ui.screens.VideoPlayerOverlay(
            viewModel = moviesViewModel,
            onMovieClick = { movie ->
                 navController.navigate(Screen.MovieDetail.createRoute(movie.id))
            }
        )
    }
}