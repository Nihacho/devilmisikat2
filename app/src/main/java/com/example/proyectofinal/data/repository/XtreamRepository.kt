package com.example.proyectofinal.data.repository

import com.example.proyectofinal.data.model.Movie
import com.example.proyectofinal.data.model.XtreamAuthResponse
import com.example.proyectofinal.data.model.XtreamSeriesInfo
import com.example.proyectofinal.data.model.XtreamStream
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class XtreamRepository {

    private var apiService: XtreamApiService? = null
    private var baseUrl: String = ""
    private var username: String = ""
    private var password: String = ""

    suspend fun login(url: String, user: String, pass: String): Result<XtreamAuthResponse> {
        return try {
            val validUrl = if (url.endsWith("/")) url else "$url/"
            
            val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            val client = OkHttpClient.Builder().addInterceptor(logging).build()

            val retrofit = Retrofit.Builder()
                .baseUrl(validUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(XtreamApiService::class.java)
            val response = service.authenticate(user, pass)

            if (response.isSuccessful && response.body() != null) {
                // Initialize class properties
                this.apiService = service
                this.baseUrl = validUrl
                this.username = user
                this.password = pass
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Login failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLiveStreams(): List<Movie> {
        val service = this.apiService ?: return emptyList()
        try {
             // ... existing code ...
            val response = service.getLiveStreams(this.username, this.password)
            if (response.isSuccessful) {
                return response.body()?.map { it.toMovie(this.baseUrl, this.username, this.password, "Live") } ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    suspend fun getVodStreams(): List<Movie> {
        val service = this.apiService ?: return emptyList()
        try {
            val response = service.getVodStreams(this.username, this.password)
            if (response.isSuccessful) {
                return response.body()?.map { it.toMovie(this.baseUrl, this.username, this.password, "VOD") } ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    suspend fun getSeries(): List<Movie> {
        val service = this.apiService ?: return emptyList()
        try {
            val response = service.getSeries(this.username, this.password)
            if (response.isSuccessful) {
                return response.body()?.map { it.toMovie(this.baseUrl, this.username, this.password, "Series") } ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    suspend fun getSeriesInfo(seriesId: String): XtreamSeriesInfo? {
        val service = this.apiService ?: return null
        try {
            val response = service.getSeriesInfo(this.username, this.password, seriesId = seriesId)
            if (response.isSuccessful) {
                return response.body()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun XtreamStream.toMovie(baseUrl: String, user: String, pass: String, type: String): Movie {
        // VOD/Series often use /movie/ or /series/ prefix or just extension change.
        // Standard Xtream:
        // Live: http://server:port/live/user/pass/id.ts
        // VOD: http://server:port/movie/user/pass/id.container_extension
        // Series: http://server:port/series/user/pass/id.container_extension
        
        val prefix = when(type) {
            "Live" -> "live"
            "VOD" -> "movie"
            "Series" -> "series"
            else -> "live"
        }
        
        // Use container_extension if available (e.g., mp4, mkv), otherwise default
        val ext = this.containerExtension ?: if (type == "Live") "ts" else "mp4"
        val extension = if (ext.startsWith(".")) ext else ".$ext"
        
        // Para series, usamos streamId como ID, pero guardamos seriesId para obtener info
        val streamUrl = "${baseUrl}$prefix/$user/$pass/${this.streamId}$extension"
        
        return Movie(
            id = if (type == "Series") "series_${this.streamId}" else this.streamId.toString(),
            title = this.name,
            logo = this.streamIcon ?: this.cover ?: "",
            url = streamUrl,
            category = if (type == "Live") "TV en Vivo" else type,
            seriesId = if (type == "Series") this.streamId.toString() else null,
            plot = this.plot
        )
    }
}
