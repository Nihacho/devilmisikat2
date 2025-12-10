package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.data.repository.XtreamRepository
import com.example.proyectofinal.ui.viewmodel.MoviesViewModel
import kotlinx.coroutines.launch

@Composable
fun IptvLoginScreen(
    viewModel: MoviesViewModel,
    onLoginSuccess: () -> Unit
) {
    var url by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    val repository = remember { XtreamRepository() }
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // Gradient Background
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1E1E),
                        Color(0xFF141414)
                    )
                )
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Xtream Codes",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color(0xFFD32F2F)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Conecta tu servicio IPTV",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(48.dp))

            // URL Field
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                label = { Text("URL del Servidor") },
                placeholder = { Text("http://...") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2C2C2C),
                    unfocusedContainerColor = Color(0xFF1E1E1E),
                    focusedBorderColor = Color(0xFFD32F2F),
                    unfocusedBorderColor = Color(0xFF3C3C3C),
                    focusedLabelColor = Color(0xFFD32F2F),
                    unfocusedLabelColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFD32F2F)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Username Field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2C2C2C),
                    unfocusedContainerColor = Color(0xFF1E1E1E),
                    focusedBorderColor = Color(0xFFD32F2F),
                    unfocusedBorderColor = Color(0xFF3C3C3C),
                    focusedLabelColor = Color(0xFFD32F2F),
                    unfocusedLabelColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFD32F2F)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2C2C2C),
                    unfocusedContainerColor = Color(0xFF1E1E1E),
                    focusedBorderColor = Color(0xFFD32F2F),
                    unfocusedBorderColor = Color(0xFF3C3C3C),
                    focusedLabelColor = Color(0xFFD32F2F),
                    unfocusedLabelColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFD32F2F)
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Connect Button
            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    viewModel.setIptvLoading(true)
                    scope.launch {
                        val result = repository.login(url, username, password)
                        result.onSuccess {
                            val channels = repository.getLiveStreams()
                            val vods = repository.getVodStreams()
                            val series = repository.getSeries()
                            
                            val allContent = channels + vods + series
                            viewModel.addIptvMovies(allContent)
                            isLoading = false
                            onLoginSuccess()
                        }.onFailure {
                            isLoading = false
                            viewModel.setIptvLoading(false)
                            errorMessage = it.message
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Gray
                ),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading && url.isNotBlank() && username.isNotBlank() && password.isNotBlank()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (!isLoading)
                                Modifier.background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFFE53935), Color(0xFFB71C1C))
                                    )
                                )
                            else Modifier.background(Color.Gray)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            "CONECTAR",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                    }
                }
            }

            errorMessage?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF5C1919)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        it,
                        color = Color(0xFFFFCDD2),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
