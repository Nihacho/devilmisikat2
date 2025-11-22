package com.example.proyectofinal.data.repository

import com.example.proyectofinal.data.model.Movie
import com.example.proyectofinal.data.parser.M3UParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MovieRepository {

    private val parser = M3UParser()
    
    // URL de ejemplo (puedes cambiarla por una real)
    private val m3uUrl = "https://iptv-org.github.io/iptv/index.m3u" 

    suspend fun getMovies(): Result<List<Movie>> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(m3uUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connect()

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val movies = parser.parse(inputStream)
                    Result.success(movies)
                } else {
                    Result.failure(Exception("Failed to connect: ${connection.responseCode}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
