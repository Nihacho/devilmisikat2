package com.example.proyectofinal.ui.navigation
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectofinal.data.model.UserRole
import com.example.proyectofinal.data.model.Movie // <--- Added this import
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
    object SeriesDetail : Screen("series_detail/{seriesId}") {
        fun createRoute(seriesId: String) = "series_detail/$seriesId"
    }
    object EpisodePlayer : Screen("episode_player/{episodeId}") {
        fun createRoute(episodeId: String) = "episode_player/$episodeId"
    }
    object IptvLogin : Screen("iptv_login")
    object IptvContent : Screen("iptv_content")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation(
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    var currentUserRole by remember { mutableStateOf<UserRole?>(null) }

    // Shared ViewModel con Application context
    val context = LocalContext.current
    val moviesViewModel: MoviesViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as android.app.Application
        )
    )

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
                    onNavigateToIptv = {
                        navController.navigate(Screen.IptvLogin.route)
                    },
                    onMovieClick = { movie ->
                        // Detectar si es serie o película/canal
                        if (movie.seriesId != null) {
                            navController.navigate(Screen.SeriesDetail.createRoute(movie.seriesId))
                        } else {
                            navController.navigate(Screen.MovieDetail.createRoute(movie.id))
                        }
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

            // IPTV Login
            composable(Screen.IptvLogin.route) {
                com.example.proyectofinal.ui.screens.IptvLoginScreen(
                    viewModel = moviesViewModel,
                    onLoginSuccess = {
                        navController.navigate(Screen.IptvContent.route) {
                            popUpTo(Screen.IptvLogin.route) { inclusive = true }
                        }
                    }
                )
            }

            // IPTV Content (Tabs: TV, Movies, Series)
            composable(Screen.IptvContent.route) {
                com.example.proyectofinal.ui.screens.IptvScreen(
                    viewModel = moviesViewModel,
                    onMovieClick = { movie ->
                        if (movie.seriesId != null) {
                            navController.navigate(Screen.SeriesDetail.createRoute(movie.seriesId))
                        } else {
                            navController.navigate(Screen.MovieDetail.createRoute(movie.id))
                        }
                    }
                )
            }

            // Series Detail Screen
            composable(
                route = Screen.SeriesDetail.route,
                arguments = listOf(
                    navArgument("seriesId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val seriesId = backStackEntry.arguments?.getString("seriesId") ?: ""
                val series = moviesViewModel.movies.collectAsState().value.find { it.seriesId == seriesId }
                if (series != null) {
                    com.example.proyectofinal.ui.screens.SeriesDetailScreen(
                        viewModel = moviesViewModel,
                        series = series,
                        onBack = {
                            navController.popBackStack()
                        },
                        onEpisodeClick = { episodeUrl, episodeTitle ->
                            // Crear Movie temporal para el episodio
                            val episodeMovie = Movie(
                                id = "episode_${System.currentTimeMillis()}",
                                title = episodeTitle,
                                logo = series.logo,
                                url = episodeUrl,
                                category = "Series"
                            )
                            moviesViewModel.setTemporaryMovie(episodeMovie)
                            navController.navigate(Screen.EpisodePlayer.createRoute(episodeMovie.id))
                        }
                    )
                }
            }

            // Episode Player Screen
            composable(
                route = Screen.EpisodePlayer.route,
                arguments = listOf(
                    navArgument("episodeId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val episodeId = backStackEntry.arguments?.getString("episodeId") ?: ""
                
                MovieDetailScreen(
                    viewModel = moviesViewModel,
                    movieId = episodeId,
                    onBack = {
                        navController.popBackStack()
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
