package com.example.proyectofinal.data.repository

import com.example.proyectofinal.data.model.User
import com.example.proyectofinal.data.model.UserRole
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Obtener usuario actual
    suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        val uid = firebaseUser.uid
        
        return try {
            val userDoc = firestore.collection("users").document(uid).get().await()
            val roleString = userDoc.getString("role") ?: "USER"
            val role = try { UserRole.valueOf(roleString) } catch (e: Exception) { UserRole.USER }
            val firstName = userDoc.getString("firstName") ?: ""
            val lastName = userDoc.getString("lastName") ?: ""
            
            User(
                uid = uid,
                email = firebaseUser.email ?: "",
                firstName = firstName,
                lastName = lastName,
                role = role
            )
        } catch (e: Exception) {
            // Fallback if firestore fails
            User(uid = uid, email = firebaseUser.email ?: "")
        }
    }

    // Login con Email/Password
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
            val firstName = userDoc.getString("firstName") ?: ""
            val lastName = userDoc.getString("lastName") ?: ""

            val user = User(
                uid = uid,
                email = email,
                firstName = firstName,
                lastName = lastName,
                role = role
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Login con Credencial (Google/GitHub)
    suspend fun signInWithCredential(credential: AuthCredential): Result<User> {
        return try {
            val result = auth.signInWithCredential(credential).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("No user ID"))
            val firebaseUser = result.user
            val email = firebaseUser?.email ?: ""
            
            // Intentar obtener nombre del proveedor si es posible
            val displayName = firebaseUser?.displayName ?: ""
            val nameParts = displayName.split(" ")
            val provFirstName = if (nameParts.isNotEmpty()) nameParts[0] else ""
            val provLastName = if (nameParts.size > 1) displayName.substringAfter(" ") else ""

            // Verificar si existe en Firestore, si no, crearlo
            val userDoc = firestore.collection("users").document(uid).get().await()
            var role = UserRole.USER
            var firstName = provFirstName
            var lastName = provLastName
            
            if (!userDoc.exists()) {
                val userData = hashMapOf(
                    "email" to email,
                    "role" to role.name,
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "createdAt" to System.currentTimeMillis()
                )
                firestore.collection("users").document(uid).set(userData).await()
            } else {
                val roleString = userDoc.getString("role") ?: "USER"
                role = try {
                    UserRole.valueOf(roleString)
                } catch (e: Exception) {
                    UserRole.USER
                }
                firstName = userDoc.getString("firstName") ?: firstName
                lastName = userDoc.getString("lastName") ?: lastName
            }

            val user = User(
                uid = uid,
                email = email,
                firstName = firstName,
                lastName = lastName,
                role = role
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Registro Extendido
    suspend fun register(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        role: UserRole = UserRole.USER
    ): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("No user ID"))

            // Guardar datos extendidos en Firestore
            val userData = hashMapOf(
                "email" to email,
                "firstName" to firstName,
                "lastName" to lastName,
                "role" to role.name,
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("users").document(uid).set(userData).await()

            val user = User(
                uid = uid,
                email = email,
                firstName = firstName,
                lastName = lastName,
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