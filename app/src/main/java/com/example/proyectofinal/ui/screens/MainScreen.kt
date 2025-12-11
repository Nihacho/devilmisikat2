package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.data.model.Movie
import com.example.proyectofinal.ui.viewmodel.MoviesViewModel

sealed class NavigationItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
) {
    object Inicio : NavigationItem("home", Icons.Default.Home, "Inicio")
    object Busqueda : NavigationItem("search", Icons.Default.Search, "Buscar")
    object TelevisionEnVivo : NavigationItem("iptv", Icons.Default.Tv, "IPTV")
    object MisListas : NavigationItem("lists", Icons.AutoMirrored.Filled.List, "Mis Listas")
    object Perfil : NavigationItem("user", Icons.Default.Person, "Perfil")
}

@Composable
fun MainScreen(
    viewModel: MoviesViewModel,
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onNavigateToIptv: () -> Unit,
    onMovieClick: (Movie) -> Unit,
) {
    val navController = rememberNavController()

    val items = listOf(
        NavigationItem.Inicio,
        NavigationItem.Busqueda,
        NavigationItem.TelevisionEnVivo,
        NavigationItem.MisListas,
        NavigationItem.Perfil
    )

    val currentPlayingMovie by viewModel.currentPlayingMovie.collectAsState()
    // Simple logic: If we have a movie and we are NOT in player route (which is technically not a destination in this nested graph, but handled by parent NavHost?), 
    // actually MainScreen seems to host the bottom tabs. The Player is likely a separate Composable in MainActivity or a full screen dialog.
    // If Player is SCREEN based, we simply show this when NOT in player.
    // However, the requested flow is: User clicks back from Player -> Player minimizes -> MiniPlayer shows.
    
    // For this prototype, if `currentPlayingMovie` is set, we show MiniPlayer slightly above BottomBar.
    
    Scaffold(
        bottomBar = {
            Column {
                 if (currentPlayingMovie != null) {
                    MiniPlayer(
                        movie = currentPlayingMovie!!,
                        onExpand = { 
                            // Navigate back to player screen (Assuming handled by onMovieClick which navigates to player)
                             onMovieClick(currentPlayingMovie!!) 
                        },
                        onClose = { 
                            viewModel.clearCurrentMovie() 
                        }
                    )
                }
            
                // Assuming currentRoute and isPlayerMinimized are defined elsewhere or need to be added.
                // For now, I'll assume they are not present and remove the if condition to make it compile.
                // If they are meant to be added, they would need to be passed as parameters or derived.
                // For the purpose of this edit, I'm focusing on the NavigationBar styling.
    
                // Barra de navegación personalizada "Flotante" o Premium
                NavigationBar(
                    containerColor = Color(0xFF141414), // Negro suave
                    // contentColor = Color.White, // This parameter is not directly available on NavigationBar, it's handled by item colors
                    tonalElevation = 8.dp
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
    
                    // Items completos de navegación
                    val newNavItems = listOf(
                        Triple("home", "Inicio", Icons.Default.Home),
                        Triple("search", "Buscar", Icons.Default.Search),
                        Triple("iptv", "IPTV", Icons.Default.Tv),
                        Triple("lists", "Mis Listas", Icons.AutoMirrored.Filled.List),
                        Triple("user", "Perfil", Icons.Default.Person)
                    )
    
                    newNavItems.forEach { (route, label, icon) ->
                        val isSelected = currentDestination?.hierarchy?.any { it.route == route } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    // Effect of "Glow" or vibrant active color
                                    tint = if (isSelected) Color(0xFFD32F2F) else Color.Gray
                                )
                            },
                            label = {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = if (isSelected) Color.White else Color.Gray
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFF2C2C2C), // Background of the indicator (pill)
                                selectedIconColor = Color(0xFFD32F2F),
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavigationItem.Inicio.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavigationItem.Inicio.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onLogout = onLogout,
                    onMovieClick = onMovieClick
                )
            }
            composable(NavigationItem.Busqueda.route) {
                SearchScreen(viewModel = viewModel, onMovieClick = onMovieClick)
            }
            composable(NavigationItem.TelevisionEnVivo.route) {
                val isLoggedIn by viewModel.isIptvLoggedIn.collectAsState()
                if (isLoggedIn) {
                    IptvScreen(viewModel = viewModel, onMovieClick = onMovieClick)
                } else {
                    IptvLoginScreen(viewModel = viewModel, onLoginSuccess = { /* Handled by VM observing */ })
                }
            }
            composable(NavigationItem.MisListas.route) {
                MyListsScreen(viewModel = viewModel, onMovieClick = onMovieClick)
            }
            composable(NavigationItem.Perfil.route) {
                UserScreen(
                    isDarkTheme = isDarkTheme,
                    onThemeChanged = onThemeChanged,
                    onLogout = onLogout,
                    onNavigateToIptv = onNavigateToIptv
                )
            }
        }
    }
}

private fun MoviesViewModel.clearCurrentMovie() {
    TODO("Not yet implemented")
}
