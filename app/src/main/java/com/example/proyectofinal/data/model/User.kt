package com.example.proyectofinal.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val role: UserRole = UserRole.USER
)

enum class UserRole {
    USER,      // Usuario normal - ve películas
    ADMIN      // Administrador - ve sensores
}