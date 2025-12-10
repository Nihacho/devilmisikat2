package com.example.proyectofinal.ui.screens


import androidx.compose.foundation.ExperimentalFoundationApi // <--- Add this
import androidx.compose.foundation.combinedClickable         // <--- Add this
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.material.icons.filled.Tv
import com.example.proyectofinal.data.model.Movie
import com.example.proyectofinal.ui.viewmodel.MoviesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: MoviesViewModel,
    onLogout: () -> Unit,
    onMovieClick: (Movie) -> Unit
) {
    val movies by viewModel.movies.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("App Películas") },
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Salir")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp) // Padding for bottom navigation
                ) {
                    // Logo Section - Premium Header
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        ) {
                            // Gradient Background
                            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                                drawRect(
                                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = listOf(
                                            androidx.compose.ui.graphics.Color(0xFF1E1E1E),
                                            androidx.compose.ui.graphics.Color.Transparent
                                        )
                                    )
                                )
                            }
                            
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "DEVILMISIKAT",
                                    style = MaterialTheme.typography.displayMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 2.sp
                                    ),
                                    color = androidx.compose.ui.graphics.Color(0xFFD32F2F)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Tu entretenimiento premium",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = androidx.compose.ui.graphics.Color.Gray
                                )
                            }
                        }
                    }
                    
                    // Popular (Always at top)
                    item {
                        SectionTitle(title = "Tendencias")
                        MovieRow(movies = movies.take(10), onMovieClick = onMovieClick)
                    }

                    // Dynamic Categories (Countries, Genres, etc.)
                    // Exclude "Tendencias" or generic if needed, but here we just list all unique categories
                    val categories = movies.map { it.category }.distinct().filter { it != "Tendencias" && it != "IPTV" }.sorted()
                    
                    items(categories) { category ->
                         val categoryMovies = movies.filter { it.category == category }
                         if (categoryMovies.isNotEmpty()) {
                             SectionTitle(title = category)
                             MovieRow(movies = categoryMovies, onMovieClick = onMovieClick)
                         }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun MovieRow(movies: List<Movie>, onMovieClick: (Movie) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(movies) { movie ->
            MovieCard(movie = movie, onClick = { onMovieClick(movie) })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieCard(
    movie: Movie, 
    onClick: (Movie) -> Unit,
    onLongClick: ((Movie) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(200.dp)
            .combinedClickable(
                onClick = { onClick(movie) },
                onLongClick = { onLongClick?.invoke(movie) }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = movie.logo,
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop,
                error = rememberVectorPainter(Icons.Default.Tv),
                placeholder = rememberVectorPainter(Icons.Default.Tv)
            )
            Text(
                text = movie.title,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}
