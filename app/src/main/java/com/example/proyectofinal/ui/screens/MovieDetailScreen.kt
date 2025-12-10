package com.example.proyectofinal.ui.screens

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.proyectofinal.ui.viewmodel.MoviesViewModel

@OptIn(UnstableApi::class)
@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun MovieDetailScreen(
    viewModel: MoviesViewModel,
    movieId: String,
    onBack: () -> Unit
) {
    val movie = viewModel.getMovieById(movieId)
    
    val context = LocalContext.current
    val exoPlayer = remember {
        val httpDataSourceFactory = androidx.media3.datasource.DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(15000)

        val dataSourceFactory = androidx.media3.datasource.DefaultDataSource.Factory(context, httpDataSourceFactory)

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(androidx.media3.exoplayer.source.DefaultMediaSourceFactory(dataSourceFactory))
            .build().apply {
            playWhenReady = true
        }
    }
    
    var showAddToListDialog by remember { mutableStateOf(false) }
    val userLists by viewModel.userLists.collectAsState()

    LaunchedEffect(movie) {
        if (movie != null) {
            val mediaItem = MediaItem.fromUri(movie.url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    if (movie != null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(movie.title) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    },
                    actions = {
                         IconButton(onClick = { showAddToListDialog = true }) {
                             Icon(Icons.Default.Add, contentDescription = "Agregar a lista")
                         }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Video Player
                AndroidView(
                    factory = {
                        PlayerView(context).apply {
                            player = exoPlayer
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Info
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = movie.title, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "Categoría: ${movie.category}", style = MaterialTheme.typography.bodyMedium)
                    
                    if (movie.logo != null) {
                        Text("Logo URL: ${movie.logo}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            
            if (showAddToListDialog) {
                AddToListDialog(
                    lists = userLists.keys.toList(),
                    onDismiss = { showAddToListDialog = false },
                    onSelect = { listName ->
                         viewModel.addMovieToList(listName, movie)
                         showAddToListDialog = false
                    }
                )
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Text("Película no encontrada", modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
        }
    }
}

@Composable
fun AddToListDialog(
    lists: List<String>,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar a lista") },
        text = {
            Column {
                lists.forEach { listName ->
                    TextButton(
                        onClick = { onSelect(listName) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(listName)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
