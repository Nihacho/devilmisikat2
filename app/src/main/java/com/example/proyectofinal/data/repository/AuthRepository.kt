package com.example.proyectofinal.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.proyectofinal.data.model.User

class AuthRepository {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    suspend fun login(email: String, pass: String): Result<User> {
        return try {
            auth.signInWithEmailAndPassword(email, pass).await()
            val user = getCurrentUser()
            if (user != null) Result.success(user) else Result.failure(Exception("User not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, pass: String, firstName: String, lastName: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, pass).await()
            val firebaseUser = authResult.user ?: throw Exception("Registration failed")
            
            val newUser = User(
                uid = firebaseUser.uid,
                email = email,
                firstName = firstName,
                lastName = lastName,
                role = com.example.proyectofinal.data.model.UserRole.USER
            )

            firestore.collection("users").document(firebaseUser.uid).set(newUser).await()
            Result.success(newUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithCredential(credential: com.google.firebase.auth.AuthCredential): Result<User> {
        return try {
            auth.signInWithCredential(credential).await()
            // Check if user exists in Firestore, if not create basic profile? 
            // For now just get current or create minimal
            val user = getCurrentUser() ?: run {
                 // Fallback if firestore doesn't have it yet (first social login)
                 val firebaseUser = auth.currentUser!!
                 val newUser = User(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    firstName = firebaseUser.displayName?.split(" ")?.firstOrNull() ?: "User",
                    lastName = firebaseUser.displayName?.split(" ")?.lastOrNull() ?: "",
                    role = com.example.proyectofinal.data.model.UserRole.USER
                 )
                 firestore.collection("users").document(firebaseUser.uid).set(newUser).await()
                 newUser
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return try {
            val document = firestore.collection("users").document(firebaseUser.uid).get().await()
            document.toObject(User::class.java) ?: User(
                uid = firebaseUser.uid, 
                email = firebaseUser.email ?: "",
                firstName = "", 
                lastName = "",
                role = com.example.proyectofinal.data.model.UserRole.USER
            )
        } catch (e: Exception) {
            // Fallback for offline or sync issues
             User(
                uid = firebaseUser.uid, 
                email = firebaseUser.email ?: "",
                firstName = "", 
                lastName = "",
                role = com.example.proyectofinal.data.model.UserRole.USER
            )
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun updateUserProfile(firstName: String, lastName: String) {
        val user = auth.currentUser ?: return // Ensure user is logged in

        // Create a map of data to update
        val userMap = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName
        )

        // Update the document in the 'users' collection corresponding to the user's ID
        firestore.collection("users")
            .document(user.uid)
            .set(userMap, com.google.firebase.firestore.SetOptions.merge())
            .await()
    }
}
