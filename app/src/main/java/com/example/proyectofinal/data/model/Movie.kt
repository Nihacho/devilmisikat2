package com.example.proyectofinal.data.model

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("backdrop_path")
    val backdropPath: String?,
    @SerializedName("release_date")
    val releaseDate: String,
    @SerializedName("vote_average")
    val voteAverage: Double,
    val popularity: Double
) {
    // Helper para obtener URL completa del poster
    fun getPosterUrl(): String {
        return "https://image.tmdb.org/t/p/w500${posterPath}"
    }

    fun getBackdropUrl(): String {
        return "https://image.tmdb.org/t/p/w780${backdropPath}"
    }
}