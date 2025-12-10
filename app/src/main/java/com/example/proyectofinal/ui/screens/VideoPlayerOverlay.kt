package com.example.proyectofinal.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.proyectofinal.data.model.Movie
import com.example.proyectofinal.ui.viewmodel.MoviesViewModel

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerOverlay(
    viewModel: MoviesViewModel,
    onMovieClick: (Movie) -> Unit
) {
    val currentMovie by viewModel.currentPlayingMovie.collectAsState()
    val isMinimized by viewModel.isPlayerMinimized.collectAsState()
    val context = LocalContext.current

    // Inicialización del ExoPlayer con configuración de red robusta
    val exoPlayer = remember(context) {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(15000)

        val dataSourceFactory = DefaultDataSource.Factory(context, httpDataSourceFactory)

        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build().apply {
                playWhenReady = true
            }
    }

    // Liberar recursos cuando el Composable se destruye
    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    // Estado de reproducción
    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }

    DisposableEffect(exoPlayer) {
        val listener = object : androidx.media3.common.Player.Listener {
            override fun onIsPlayingChanged(isPlayingVal: Boolean) {
                isPlaying = isPlayingVal
            }
        }
        exoPlayer.addListener(listener)
        onDispose { exoPlayer.removeListener(listener) }
    }

    // Cargar la película cuando cambia
    LaunchedEffect(currentMovie) {
        currentMovie?.let {
            val mediaItem = MediaItem.fromUri(it.url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            if (!isMinimized) exoPlayer.play()
        }
    }

    // Solo mostrar si hay una película seleccionada
    if (currentMovie != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {

            // Player Expandido (Pantalla completa o modo detalle)
            AnimatedVisibility(
                visible = !isMinimized,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.fillMaxSize() // Asegura que ocupe todo el espacio
            ) {
                ExpandedPlayer(viewModel = viewModel, exoPlayer = exoPlayer, onMovieClick = onMovieClick)
            }

            // Mini Player (Barra inferior)
            AnimatedVisibility(
                visible = isMinimized,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                MiniPlayer(
                    viewModel = viewModel, 
                    exoPlayer = exoPlayer,
                    isPlaying = isPlaying,
                    onPlayPause = {
                        if (isPlaying) exoPlayer.pause() else exoPlayer.play()
                    }
                )
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun ExpandedPlayer(
    viewModel: MoviesViewModel,
    exoPlayer: ExoPlayer,
    onMovieClick: (Movie) -> Unit
) {
    val movie by viewModel.currentPlayingMovie.collectAsState()
    if (movie == null) {
        viewModel.closePlayer()
        return
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = remember(context) { context.findActivity() }
    var isFullscreen by remember { mutableStateOf(false) }

    // Manejo del ciclo de vida y orientación
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> exoPlayer.pause()
                Lifecycle.Event.ON_RESUME -> exoPlayer.play()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            // Restaurar orientación al salir
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    // Cambiar orientación física al activar fullscreen
    LaunchedEffect(isFullscreen) {
        activity?.requestedOrientation = if (isFullscreen) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                // Gesto para minimizar arrastrando hacia abajo
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount > 50 && !isFullscreen) {
                        viewModel.minimizePlayer()
                    }
                }
            }
    ) {
        if (isFullscreen) {
            // MODO PANTALLA COMPLETA
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        useController = true
                    }
                },
                update = { pv ->
                    pv.player = exoPlayer
                },
                modifier = Modifier.fillMaxSize()
            )

            // Botón para salir de fullscreen
            IconButton(
                onClick = { isFullscreen = false },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Default.FullscreenExit, contentDescription = "Salir Fullscreen", tint = Color.White)
            }

        } else {
            // MODO EXPANDIDO (Vertical con detalles)
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                                useController = true
                            }
                        },
                        update = { pv ->
                            pv.player = exoPlayer
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Botón para minimizar
                    IconButton(
                        onClick = { viewModel.minimizePlayer() },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Minimizar", tint = Color.White)
                    }

                    // Botón para entrar a fullscreen
                    IconButton(
                        onClick = { isFullscreen = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Icon(Icons.Default.Fullscreen, contentDescription = "Fullscreen", tint = Color.White)
                    }
                }

                // Detalles de la película
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = movie!!.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White
                    )
                    Text(
                        // Corregido: Se eliminó la referencia a 'country'
                        text = movie!!.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Más videos",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    // Aquí se podrían mostrar recomendaciones futuras
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun MiniPlayer(
    viewModel: MoviesViewModel,
    exoPlayer: ExoPlayer,
    isPlaying: Boolean,
    onPlayPause: () -> Unit
) {
    val movie by viewModel.currentPlayingMovie.collectAsState()

    // Nota: Padding bottom se suele usar para evitar solapamiento con la barra de navegación inferior
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp) // Altura fija
            .padding(8.dp)
            .padding(bottom = 0.dp) // Ajustar según si tienes NavigationBar
            .clickable { viewModel.maximizePlayer() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            // Vista previa del video en el mini player
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        useController = false // Sin controles en modo mini
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM // Zoom para llenar el cuadro
                    }
                },
                update = { pv ->
                    pv.player = exoPlayer
                },
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
            )

            // Texto del título
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = movie?.title ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isPlaying) "Reproduciendo..." else "Pausado",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Botón Play/Pause
            IconButton(onClick = onPlayPause) {
                Icon(
                    imageVector = if (isPlaying) androidx.compose.material.icons.Icons.Default.Pause else androidx.compose.material.icons.Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pausar" else "Reproducir"
                )
            }

            // Botón Cerrar
            IconButton(onClick = { viewModel.closePlayer() }) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
        }
    }
}

// Función de extensión auxiliar para encontrar la Activity desde un Contexto
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
