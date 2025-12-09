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
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
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

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }
    
    LaunchedEffect(currentMovie) {
        currentMovie?.let {
            val mediaItem = MediaItem.fromUri(it.url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    if (currentMovie != null) {
        AnimatedVisibility(
            visible = !isMinimized,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            ExpandedPlayer(viewModel = viewModel, exoPlayer = exoPlayer, onMovieClick = onMovieClick)
        }
        
        AnimatedVisibility(
            visible = isMinimized,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            MiniPlayer(viewModel = viewModel, exoPlayer = exoPlayer)
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
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val activity = remember(context) { context.findActivity() }
    var isFullscreen by remember { mutableStateOf(false) }

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
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    if (dragAmount > 50) {
                        viewModel.minimizePlayer()
                    }
                }
            }
    ) {
        if (isFullscreen) {
            AndroidView(
                factory = { ctx ->
                    val view = android.view.LayoutInflater.from(ctx).inflate(com.example.proyectofinal.R.layout.view_player, null) as PlayerView
                    view
                },
                update = { 
                    it.player = exoPlayer
                    it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    it.useController = true
                },
                onRelease = { it.player = null },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f).background(Color.Black)) {
                    AndroidView(
                        factory = { ctx ->
                            val view = android.view.LayoutInflater.from(ctx).inflate(com.example.proyectofinal.R.layout.view_player, null) as PlayerView
                            view
                        },
                        update = { 
                            it.player = exoPlayer 
                            it.useController = true
                        },
                        onRelease = { it.player = null },
                        modifier = Modifier.fillMaxSize()
                    )
                    IconButton(
                        onClick = { viewModel.closePlayer() },
                        modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                    }
                }
                
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = movie!!.title, style = MaterialTheme.typography.headlineSmall, color = Color.White)
                    Text(text = "${movie!!.country} • ${movie!!.category}", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Más videos", style = MaterialTheme.typography.titleMedium, color = Color.White)
                    // Aquí se podrían mostrar recomendaciones
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun MiniPlayer(
    viewModel: MoviesViewModel,
    exoPlayer: ExoPlayer
) {
    val movie by viewModel.currentPlayingMovie.collectAsState()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(8.dp)
            .clickable { viewModel.maximizePlayer() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            AndroidView(
                factory = { ctx ->
                    val view = android.view.LayoutInflater.from(ctx).inflate(com.example.proyectofinal.R.layout.view_player, null) as PlayerView
                    view
                },
                update = { 
                    it.player = exoPlayer
                    it.useController = false
                    it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                },
                onRelease = { it.player = null },
                modifier = Modifier.width(100.dp).fillMaxHeight()
            )
            
            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                Text(
                    text = movie?.title ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Reproduciendo...",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            IconButton(onClick = { viewModel.closePlayer() }) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
        }
    }
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
