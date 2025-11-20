package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.data.model.UserRole
import com.example.proyectofinal.data.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
    val primaryColor = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Fondo Superior con Ola Personalizada
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp) // Altura suficiente para la curva
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(width, 0f)
                        lineTo(width, height * 0.65f) // Punto inicial derecho (más alto)
                        // Curva de Bezier cúbica para la ola suave
                        cubicTo(
                            width * 0.7f, height * 0.65f, // Primer punto de control
                            width * 0.4f, height * 1.1f,  // Segundo punto de control (baja)
                            0f, height * 0.75f            // Punto final izquierdo
                        )
                        lineTo(0f, 0f) // Cerrar arriba a la izquierda
                        close()
                    }

                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                tertiaryColor, // Parte superior más oscura
                                primaryColor   // Parte inferior más brillante
                            ),
                            startY = 0f,
                            endY = height
                        )
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Espaciado para que el avatar quede sobre la ola
                Spacer(modifier = Modifier.height(160.dp))

                // Avatar Icon (Superpuesto en la ola)
                Surface(
                    shape = CircleShape,
                    shadowElevation = 6.dp,
                    color = Color(0xFFE0E0E0), // Gris muy suave para el círculo exterior
                    modifier = Modifier.size(100.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "User Avatar",
                        tint = Color.Gray,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(0.dp) // Icono llena el círculo
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Título "Sign In" (Playfair Display - Headline Large)
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.headlineLarge, // Playfair Display Black
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Formulario
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Email (Lato - Body Large)
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = { 
                            Text(
                                "Email", 
                                style = MaterialTheme.typography.bodyMedium, // Lato Regular 14sp
                                color = Color(0xFF666666)
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge, // Lato Regular 16sp
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFEEEEEE), // Gris muy claro
                            unfocusedContainerColor = Color(0xFFEEEEEE),
                            focusedBorderColor = Color.Transparent, // Sin borde visible al enfocar
                            unfocusedBorderColor = Color.Transparent // Sin borde visible normal
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password (Lato - Body Large)
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = { 
                            Text(
                                "Password", 
                                style = MaterialTheme.typography.bodyMedium, // Lato Regular 14sp
                                color = Color(0xFF666666)
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        textStyle = MaterialTheme.typography.bodyLarge, // Lato Regular 16sp
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFEEEEEE),
                            unfocusedContainerColor = Color(0xFFEEEEEE),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Forgot Password (Lato - Label Small)
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = "Forgot your password?",
                            style = MaterialTheme.typography.labelSmall, // Lato Medium 12sp
                            color = Color.Gray,
                            modifier = Modifier.clickable { /* TODO */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // Botón Sign In (Lato - Label Large)
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .shadow(4.dp, RoundedCornerShape(25.dp)),
                        enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                        shape = RoundedCornerShape(25.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFCC0000), // Rojo fuerte manual para asegurar
                            contentColor = Color.White,
                            disabledContainerColor = Color(0xFFCC0000).copy(alpha = 0.6f)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White
                            )
                        } else {
                            Text(
                                "Sign In",
                                style = MaterialTheme.typography.labelLarge, // Lato Bold 16sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Or Divider (Lato - Body Medium for "Or")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                        Text(
                            text = " Or ",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF666666)),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Social Icons (Lato - Label Medium 14sp aproximado)
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SocialIcon(text = "G", color = Color(0xFFDB4437)) // Google
                        Spacer(modifier = Modifier.width(24.dp))
                        SocialIcon(text = "f", color = Color(0xFF4267B2)) // Facebook
                        Spacer(modifier = Modifier.width(24.dp))
                        SocialIcon(text = "in", color = Color(0xFF0077B5)) // LinkedIn
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Sign Up Text (Lato - Label Small)
                    Row(
                        modifier = Modifier.padding(bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Don't have an account? ",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                        )
                        Text(
                            text = "Sign Up",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFFCC0000),
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.clickable {
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
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SocialIcon(text: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(12.dp), // Iconos ligeramente cuadrados como en diseños modernos o circular si prefieres
        shadowElevation = 4.dp,
        color = Color.White,
        modifier = Modifier
            .size(50.dp)
            .clickable { /* TODO */ }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(fontSize = 24.sp), // Usamos Lato Bold pero más grande para el icono
                color = color
            )
        }
    }
}

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