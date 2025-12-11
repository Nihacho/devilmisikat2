package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Tv
import coil.compose.AsyncImage
import com.example.proyectofinal.data.model.Movie
import com.example.proyectofinal.data.model.XtreamEpisode
import com.example.proyectofinal.data.model.XtreamSeriesInfo
import com.example.proyectofinal.ui.viewmodel.MoviesViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeriesDetailScreen(
    viewModel: MoviesViewModel,
    series: Movie,
    onBack: () -> Unit,
    onEpisodeClick: (episodeUrl: String, episodeTitle: String) -> Unit
) {
    val scope = rememberCoroutineScope()
    var seriesInfo by remember { mutableStateOf<XtreamSeriesInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedSeason by remember { mutableStateOf(1) }

    LaunchedEffect(series.seriesId) {
        if (series.seriesId != null) {
            scope.launch {
                seriesInfo = viewModel.getSeriesInfo(series.seriesId)
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(series.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Logo de la serie
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    ) {
                        AsyncImage(
                            model = seriesInfo?.info?.cover ?: series.logo,
                            contentDescription = series.title,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            error = rememberVectorPainter(Icons.Default.Tv),
                            placeholder = rememberVectorPainter(Icons.Default.Tv)
                        )
                    }
                }

                // Información de la serie
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = seriesInfo?.info?.name ?: series.title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        if (seriesInfo?.info?.genre != null) {
                            Text(
                                text = "Género: ${seriesInfo?.info?.genre}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        
                        if (seriesInfo?.info?.rating != null) {
                            Text(
                                text = "⭐ ${seriesInfo?.info?.rating}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        if (seriesInfo?.info?.plot != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = seriesInfo?.info?.plot ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Selector de temporadas
                if (seriesInfo?.seasons?.isNotEmpty() == true) {
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Temporadas",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            
                            androidx.compose.foundation.lazy.LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(seriesInfo?.seasons ?: emptyList()) { season ->
                                    FilterChip(
                                        selected = selectedSeason == season.seasonNumber,
                                        onClick = { selectedSeason = season.seasonNumber },
                                        label = {
                                            Text(
                                                "Temporada ${season.seasonNumber}",
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = androidx.compose.ui.graphics.Color(0xFFD32F2F),
                                            selectedLabelColor = androidx.compose.ui.graphics.Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Lista de episodios de la temporada seleccionada
                val episodes = seriesInfo?.episodes?.get(selectedSeason.toString()) ?: emptyList()
                
                if (episodes.isNotEmpty()) {
                    item {
                        Text(
                            text = "Episodios",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    items(episodes.sortedBy { it.episodeNum }) { episode ->
                        EpisodeCard(
                            episode = episode,
                            series = series,
                            onClick = {
                                val episodeUrl = viewModel.buildEpisodeUrl(series.seriesId!!, episode)
                                val episodeTitle = "${series.title} - T${selectedSeason}E${episode.episodeNum}: ${episode.title ?: episode.info?.name ?: "Episodio ${episode.episodeNum}"}"
                                onEpisodeClick(episodeUrl, episodeTitle)
                            }
                        )
                    }
                } else {
                    item {
                        Text(
                            text = "No hay episodios disponibles para esta temporada",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EpisodeCard(
    episode: XtreamEpisode,
    series: Movie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Thumbnail del episodio
            Card(
                modifier = Modifier.size(80.dp, 60.dp)
            ) {
                AsyncImage(
                    model = episode.info?.movieImage ?: episode.info?.coverBig ?: series.logo,
                    contentDescription = episode.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = rememberVectorPainter(Icons.Default.Tv),
                    placeholder = rememberVectorPainter(Icons.Default.Tv)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Episodio ${episode.episodeNum}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = episode.title ?: episode.info?.name ?: "Episodio ${episode.episodeNum}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                if (episode.info?.duration != null) {
                    Text(
                        text = episode.info.duration,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
