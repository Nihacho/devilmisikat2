package com.example.proyectofinal.data.repository

import com.example.proyectofinal.data.model.User
import com.example.proyectofinal.data.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Obtener usuario actual
    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            uid = firebaseUser.uid,
            email = firebaseUser.email ?: ""
        )
    }

    // Login
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("No user ID"))

            // Obtener rol de Firestore
            val userDoc = firestore.collection("users").document(uid).get().await()
            val roleString = userDoc.getString("role") ?: "USER"
            val role = try {
                UserRole.valueOf(roleString)
            } catch (e: Exception) {
                UserRole.USER
            }

            val user = User(
                uid = uid,
                email = email,
                role = role
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Registro
    suspend fun register(
        email: String,
        password: String,
        role: UserRole = UserRole.USER
    ): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("No user ID"))

            // Guardar rol en Firestore
            val userData = hashMapOf(
                "email" to email,
                "role" to role.name,
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("users").document(uid).set(userData).await()

            val user = User(
                uid = uid,
                email = email,
                role = role
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Logout
    fun logout() {
        auth.signOut()
    }

    // Verificar si hay usuario logueado
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}