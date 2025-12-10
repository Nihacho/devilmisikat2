package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.proyectofinal.data.model.Movie
import com.example.proyectofinal.ui.viewmodel.MoviesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MoviesViewModel,
    onMovieClick: (Movie) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val movies by viewModel.movies.collectAsState()
    val categories = remember(movies) {
        movies.map { it.category }.distinct().sorted()
    }

    val searchResults = remember(query, movies) {
        viewModel.searchMovies(query)
    }

    val categoryResults = remember(selectedCategory, movies) {
        if (selectedCategory != null) {
            movies.filter { it.category == selectedCategory }
        } else {
            emptyList()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Search Bar - Premium Style
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                selectedCategory = null // Reset category when searching
            },
            label = { Text("Buscar películas") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty() || selectedCategory != null) {
                    IconButton(onClick = {
                        query = ""
                        selectedCategory = null
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = androidx.compose.ui.graphics.Color(0xFF2C2C2C),
                unfocusedContainerColor = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
                focusedBorderColor = androidx.compose.ui.graphics.Color(0xFFD32F2F),
                unfocusedBorderColor = androidx.compose.ui.graphics.Color(0xFF3C3C3C),
                focusedLabelColor = androidx.compose.ui.graphics.Color(0xFFD32F2F),
                unfocusedLabelColor = androidx.compose.ui.graphics.Color.Gray,
                focusedTextColor = androidx.compose.ui.graphics.Color.White,
                unfocusedTextColor = androidx.compose.ui.graphics.Color.White,
                cursorColor = androidx.compose.ui.graphics.Color(0xFFD32F2F)
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (query.isBlank() && selectedCategory == null) {
            // Categories
            Text("Categorías", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(category = category, onClick = { selectedCategory = category })
                }
            }
        } else {
            // Results
            val moviesToShow = if (selectedCategory != null) categoryResults else searchResults

            if (selectedCategory != null) {
                Text("Categoría: $selectedCategory", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (moviesToShow.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No se encontraron resultados")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(moviesToShow) { movie ->
                        SearchMovieItem(movie = movie, onClick = { onMovieClick(movie) })
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color(0xFF2C2C2C)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = category,
                style = MaterialTheme.typography.titleLarge,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = androidx.compose.ui.graphics.Color.White
            )
        }
    }
}

@Composable
fun SearchMovieItem(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color(0xFF1E1E1E)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                modifier = Modifier.size(60.dp)
            ) {
                AsyncImage(
                    model = movie.logo,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    ),
                    color = androidx.compose.ui.graphics.Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = androidx.compose.ui.graphics.Color.Gray
                )
            }
        }
    }
}