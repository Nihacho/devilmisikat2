package com.example.proyectofinal.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectofinal.data.model.Movie
import com.example.proyectofinal.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoviesViewModel : ViewModel() {
    private val repository = MovieRepository()

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isIptvLoading = MutableStateFlow(false)
    val isIptvLoading: StateFlow<Boolean> = _isIptvLoading.asStateFlow()

    private val _userLists = MutableStateFlow<Map<String, List<Movie>>>(
        mapOf("Favoritos" to emptyList(), "Ver más tarde" to emptyList())
    )
    val userLists: StateFlow<Map<String, List<Movie>>> = _userLists.asStateFlow()

    private val _vodMovies = MutableStateFlow<List<Movie>>(emptyList())
    val vodMovies: StateFlow<List<Movie>> = _vodMovies.asStateFlow()

    private val _series = MutableStateFlow<List<Movie>>(emptyList())
    val series: StateFlow<List<Movie>> = _series.asStateFlow()

    private val _isIptvLoggedIn = MutableStateFlow(false)
    val isIptvLoggedIn: StateFlow<Boolean> = _isIptvLoggedIn.asStateFlow()

    // ESTADO DEL REPRODUCTOR
    private val _currentPlayingMovie = MutableStateFlow<Movie?>(null)
    val currentPlayingMovie: StateFlow<Movie?> = _currentPlayingMovie.asStateFlow()

    private val _isPlayerMinimized = MutableStateFlow(false)
    val isPlayerMinimized: StateFlow<Boolean> = _isPlayerMinimized.asStateFlow()

    init {
        fetchMovies()
    }

    private fun fetchMovies() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getMovies()
            result.onSuccess { movieList ->
                val allMovies = movieList.toMutableList()
                allMovies.addAll(com.example.proyectofinal.data.datasource.MisCanales.getChannels()) // Add Public Channels
                _movies.value = allMovies
            }.onFailure {
                // Handle error
                // Even if API fails, load public channels
                _movies.value = com.example.proyectofinal.data.datasource.MisCanales.getChannels()
            }
            _isLoading.value = false
        }
    }

    fun getMovieById(id: String): Movie? {
        return _movies.value.find { it.id == id }
    }

    fun getMoviesByCategory(category: String): List<Movie> {
        return _movies.value.filter { it.category == category }
    }

    fun searchMovies(query: String): List<Movie> {
        if (query.isBlank()) return emptyList()
        return _movies.value.filter { it.title.contains(query, ignoreCase = true) }
    }

    fun createList(listName: String) {
        val currentLists = _userLists.value.toMutableMap()
        if (!currentLists.containsKey(listName)) {
            currentLists[listName] = emptyList()
            _userLists.value = currentLists
        }
    }

    fun addMovieToList(listName: String, movie: Movie) {
        val currentLists = _userLists.value.toMutableMap()
        val list = currentLists[listName]?.toMutableList() ?: mutableListOf()
        if (list.none { it.id == movie.id }) {
            list.add(movie)
            currentLists[listName] = list
            _userLists.value = currentLists
        }
    }

    // Funciones del Reproductor
    fun playMovie(movie: Movie) {
        _currentPlayingMovie.value = movie
        _isPlayerMinimized.value = false
    }

    fun closePlayer() {
        _currentPlayingMovie.value = null
        _isPlayerMinimized.value = false
    }

    fun minimizePlayer() {
        _isPlayerMinimized.value = true
    }

    fun maximizePlayer() {
        _isPlayerMinimized.value = false
    }

    // IPTV
    fun setIptvLoading(isLoading: Boolean) {
        _isIptvLoading.value = isLoading
    }

    fun addIptvMovies(newMovies: List<Movie>) {
        _isIptvLoggedIn.value = true
        _isIptvLoading.value = false // Termina carga
        val current = _movies.value.toMutableList()
        val vods = _vodMovies.value.toMutableList()
        val seriesList = _series.value.toMutableList()

        newMovies.forEach { movie ->
            // Add to specific lists for Tabs
            when (movie.category) {
                "VOD" -> vods.add(movie)
                "Series" -> seriesList.add(movie)
                else -> { /* Live TV */ } 
            }
            // Add ALL to main list for Search/Home visibility
            current.add(movie)
        }
        
        _movies.value = current
        _vodMovies.value = vods
        _series.value = seriesList
    }

    suspend fun updateUserProfile(firstName: String, lastName: String) {
        com.example.proyectofinal.data.repository.AuthRepository()
            .updateUserProfile(firstName, lastName)
    }
}