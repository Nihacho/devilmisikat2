package com.example.proyectofinal.data.model

import java.util.UUID

data class Movie(
    val id: String,
    val title: String,
    val logo: String, // <--- Add this line
    val url: String,
    val category: String
)
