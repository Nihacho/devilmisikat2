package com.example.proyectofinal.data.repository

import com.example.proyectofinal.data.model.Movie
import com.example.proyectofinal.data.remote.RetrofitInstance

class MovieRepository {

    private val api = RetrofitInstance.api
    private val apiKey = RetrofitInstance.API_KEY

    // Obtener películas populares
    suspend fun getPopularMovies(): Result<List<Movie>> {
        return try {
            val response = api.getPopularMovies(apiKey)
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener películas mejor valoradas
    suspend fun getTopRatedMovies(): Result<List<Movie>> {
        return try {
            val response = api.getTopRatedMovies(apiKey)
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Buscar películas
    suspend fun searchMovies(query: String): Result<List<Movie>> {
        return try {
            if (query.isBlank()) {
                return Result.success(emptyList())
            }
            val response = api.searchMovies(apiKey, query)
            Result.success(response.results)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener detalle de película (NUEVO)
    suspend fun getMovieDetail(movieId: Int): Result<Movie> {
        return try {
            val movie = api.getMovieDetail(movieId, apiKey)
            Result.success(movie)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}