package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectofinal.data.model.Movie
import com.example.proyectofinal.ui.viewmodel.MoviesViewModel

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun IptvScreen(
    viewModel: MoviesViewModel,
    onMovieClick: (Movie) -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("TV en Vivo", "Películas", "Series")

    val movies by viewModel.movies.collectAsState()
    val vodMovies by viewModel.vodMovies.collectAsState()
    val series by viewModel.series.collectAsState()
    val userLists by viewModel.userLists.collectAsState()

    // Context Menu State
    var showMenu by remember { mutableStateOf(false) }
    var selectedMovieForMenu by remember { mutableStateOf<Movie?>(null) }

    // Logic to select content based on Tab
    val liveContent = remember(movies) { movies.filter { it.category != "VOD" && it.category != "Series" } }
    
    val currentContent = when (selectedTabIndex) {
        0 -> liveContent
        1 -> vodMovies
        2 -> series
        else -> emptyList()
    }

    // Filter Logic
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val categories = remember(currentContent) {
        currentContent.map { it.category }.distinct().sorted()
    }
    
    // Reset filter when tab changes
    LaunchedEffect(selectedTabIndex) {
        selectedCategory = null
    }

    val isIptvLoading by viewModel.isIptvLoading.collectAsState()

    val filteredContent = remember(currentContent, selectedCategory) {
        if (selectedCategory != null) {
            currentContent.filter { it.category == selectedCategory }
        } else {
            currentContent
        }
    }

    if (isIptvLoading) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Conectando", style = MaterialTheme.typography.titleLarge) },
            text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Obteniendo listas IPTV...")
                }
            },
            confirmButton = {}
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }
            
            // Category Chips - Premium Style
            if (categories.isNotEmpty()) {
                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // "All" Chip
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { 
                                Text(
                                    "Todos",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (selectedCategory == null) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                    )
                                ) 
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = androidx.compose.ui.graphics.Color(0xFFD32F2F),
                                selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                                containerColor = androidx.compose.ui.graphics.Color(0xFF2C2C2C),
                                labelColor = androidx.compose.ui.graphics.Color.Gray
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedCategory == null,
                                borderColor = if (selectedCategory == null) androidx.compose.ui.graphics.Color(0xFFD32F2F) else androidx.compose.ui.graphics.Color.Transparent,
                                selectedBorderColor = androidx.compose.ui.graphics.Color(0xFFD32F2F),
                                borderWidth = 1.dp,
                                selectedBorderWidth = 2.dp
                            )
                        )
                    }
                    
                    items(categories) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { 
                                Text(
                                    category,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (selectedCategory == category) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                                    )
                                ) 
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = androidx.compose.ui.graphics.Color(0xFFD32F2F),
                                selectedLabelColor = androidx.compose.ui.graphics.Color.White,
                                containerColor = androidx.compose.ui.graphics.Color(0xFF2C2C2C),
                                labelColor = androidx.compose.ui.graphics.Color.Gray
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedCategory == category,
                                borderColor = if (selectedCategory == category) androidx.compose.ui.graphics.Color(0xFFD32F2F) else androidx.compose.ui.graphics.Color.Transparent,
                                selectedBorderColor = androidx.compose.ui.graphics.Color(0xFFD32F2F),
                                borderWidth = 1.dp,
                                selectedBorderWidth = 2.dp
                            )
                        )
                    }
                }
            }

            ChannelGrid(
                movies = filteredContent, 
                onMovieClick = onMovieClick,
                onLongClick = { movie ->
                    selectedMovieForMenu = movie
                    showMenu = true
                }
            )
        }
        
        // Add to List Dialog/Menu
        if (showMenu && selectedMovieForMenu != null) {
            AlertDialog(
                onDismissRequest = { showMenu = false },
                title = { Text("Agregar a Lista") },
                text = {
                    Column {
                        userLists.keys.forEach { listName ->
                            TextButton(
                                onClick = {
                                    viewModel.addMovieToList(listName, selectedMovieForMenu!!)
                                    showMenu = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(listName)
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showMenu = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun ChannelGrid(
    movies: List<Movie>, 
    onMovieClick: (Movie) -> Unit,
    onLongClick: (Movie) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(movies) { movie ->
            MovieCard(
                movie = movie, 
                onClick = { onMovieClick(movie) },
                onLongClick = { onLongClick(movie) }
            )
        }
    }
}
