package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.data.model.UserRole
import com.example.proyectofinal.data.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (UserRole) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val authRepository = remember { AuthRepository() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Movie Tracker",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Button(
            onClick = {
                isLoading = true
                errorMessage = null
                viewModel.login(
                    email = email,
                    password = password,
                    authRepository = authRepository,
                    onSuccess = { user ->
                        isLoading = false
                        onLoginSuccess(user.role)
                    },
                    onError = { error ->
                        isLoading = false
                        errorMessage = error
                    }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Iniciar Sesión")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                isLoading = true
                errorMessage = null
                viewModel.register(
                    email = email,
                    password = password,
                    authRepository = authRepository,
                    onSuccess = { user ->
                        isLoading = false
                        onLoginSuccess(user.role)
                    },
                    onError = { error ->
                        isLoading = false
                        errorMessage = error
                    }
                )
            },
            enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
        ) {
            Text("Registrarse")
        }
    }
}

// ViewModel que SÍ extiende de ViewModel (ESTO ES LO QUE FALTABA)
class LoginViewModel : ViewModel() {
    fun login(
        email: String,
        password: String,
        authRepository: AuthRepository,
        onSuccess: (com.example.proyectofinal.data.model.User) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = authRepository.login(email, password)
            result.onSuccess { user ->
                onSuccess(user)
            }.onFailure { error ->
                onError(error.message ?: "Error al iniciar sesión")
            }
        }
    }

    fun register(
        email: String,
        password: String,
        authRepository: AuthRepository,
        onSuccess: (com.example.proyectofinal.data.model.User) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = authRepository.register(email, password)
            result.onSuccess { user ->
                onSuccess(user)
            }.onFailure { error ->
                onError(error.message ?: "Error al registrarse")
            }
        }
    }
}