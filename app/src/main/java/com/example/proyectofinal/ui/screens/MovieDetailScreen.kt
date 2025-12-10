package com.example.proyectofinal.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
// SE ELIMINÓ EL IMPORT ERRÓNEO AQUÍ
import com.example.proyectofinal.data.model.Movie
import com.example.proyectofinal.ui.viewmodel.MoviesViewModel

// SE ELIMINÓ LA EXTENSIÓN 'Movie.plot' PORQUE 'description' NO EXISTE EN TU MODELO DE DATOS ACTUAL

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(UnstableApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    viewModel: MoviesViewModel,
    movieId: String,
    onBack: () -> Unit
) {
    val movie = viewModel.getMovieById(movieId)

    val context = LocalContext.current
    val activity = context as? Activity
    var isFullscreen by remember { mutableStateOf(false) }

    // Función para manejar pantalla completa
    fun toggleFullscreen(enable: Boolean) {
        isFullscreen = enable
        if (activity != null) {
            val window = activity.window
            val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

            if (enable) {
                // Entrar a Fullscreen (Landscape + Immersive)
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            } else {
                // Salir de Fullscreen (Portrait + Show Bars)
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // Manejar botón atrás en Fullscreen
    BackHandler(enabled = isFullscreen) {
        toggleFullscreen(false)
    }

    // Restaurar orientación al salir de la pantalla
    DisposableEffect(Unit) {
        onDispose {
            if (activity != null) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                val window = activity.window
                WindowCompat.getInsetsController(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

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
                if (!isFullscreen) {
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
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (isFullscreen) PaddingValues(0.dp) else paddingValues)
            ) {
                // Video Player Container
                Box(
                    modifier = if (isFullscreen) {
                        Modifier
                            .fillMaxSize()
                            .background(Color.Black)
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .background(Color.Black)
                    }
                ) {
                    AndroidView(
                        factory = {
                            PlayerView(context).apply {
                                player = exoPlayer
                                layoutParams = FrameLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                // Deshabilitar controles nativos para usar nuestro botón custom
                                useController = true
                                controllerShowTimeoutMs = 3000
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Botón Fullscreen Overlay - Mejorado con fondo
                    Surface(
                        onClick = { toggleFullscreen(!isFullscreen) },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        shape = androidx.compose.foundation.shape.CircleShape,
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Icon(
                            imageVector = if (isFullscreen) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                            contentDescription = if (isFullscreen) "Salir de Pantalla Completa" else "Pantalla Completa",
                            tint = Color.White,
                            modifier = Modifier.padding(12.dp).size(24.dp)
                        )
                    }
                }

                if (!isFullscreen) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Info
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = movie.title, style = MaterialTheme.typography.headlineLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Categoría: ${movie.category}", style = MaterialTheme.typography.bodyLarge)
                        Spacer(modifier = Modifier.height(16.dp))

                        // CORREGIDO: Se usa un string fijo porque 'movie.plot' no se pudo generar
                        Text(
                            text = "Sin descripción disponible.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
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
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Text("Película no encontrada", modifier = Modifier.align(Alignment.Center))
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
