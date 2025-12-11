package com.example.proyectofinal.data.model

import java.util.UUID

data class Movie(
    val id: String,
    val title: String,
    val logo: String,
    val url: String,
    val category: String,
    val seriesId: String? = null,  // ID de la serie para obtener temporadas/episodios
    val plot: String? = null        // Descripción/sinopsis
)
