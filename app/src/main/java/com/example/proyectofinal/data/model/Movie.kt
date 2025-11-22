package com.example.proyectofinal.data.model

import java.util.UUID

data class Movie(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val logo: String?,
    val url: String,
    val category: String = "General"
)
