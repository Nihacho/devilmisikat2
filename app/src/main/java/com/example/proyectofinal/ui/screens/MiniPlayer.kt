package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.proyectofinal.data.model.Movie

@Composable
fun MiniPlayer(
    movie: Movie,
    onExpand: () -> Unit,
    onClose: () -> Unit
) {
    // Diseño "Flotante" tipo tarjeta premium (Spotify/YouTube style)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp) // Margen flotante
            .height(72.dp) // Altura cómoda
            .clickable { onExpand() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E) // Gris Oscuro Premium
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Thumbnail / Logo
                Card(
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .aspectRatio(16f/9f)
                        .fillMaxHeight(),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                        if (movie.logo != null) {
                            AsyncImage(
                                model = movie.logo,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Placeholder si no hay logo
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Metadata
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = movie.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = movie.category ?: "Reproduciendo ahora",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB0B0B0), // Gris claro
                        maxLines = 1
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))

                // Botón Cerrar discreto
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close, 
                        contentDescription = "Cerrar", 
                        tint = Color.White
                    )
                }
            }
            
            // Barra de progreso inferior (Decorativa)
            LinearProgressIndicator(
                progress = { 1f }, // Indeterminado o "Live"
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(2.dp),
                color = Color(0xFFD32F2F), // Rojo Accent
                trackColor = Color.Transparent,
            )
        }
    }
}
