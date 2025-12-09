package com.example.proyectofinal.data.repository

import com.example.proyectofinal.data.model.Movie
import com.example.proyectofinal.data.model.XtreamAuthResponse
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
            val response = service.getLiveStreams(this.username, this.password)
            if (response.isSuccessful) {
                return response.body()?.map { it.toMovie(this.baseUrl, this.username, this.password) } ?: emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    private fun XtreamStream.toMovie(baseUrl: String, user: String, pass: String): Movie {
        val streamUrl = "${baseUrl}live/$user/$pass/${this.streamId}.ts"
        // Ensure Movie parameters match exactly with Movie.kt
        // Movie(id, title, logo, url, category)
        return Movie(
            id = this.streamId.toString(),
            title = this.name,
            logo = this.streamIcon ?: "",
            url = streamUrl,
            category = "IPTV"
        )
    }
}
