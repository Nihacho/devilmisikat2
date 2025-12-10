package com.example.proyectofinal.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.example.proyectofinal.data.model.User
import com.example.proyectofinal.data.repository.AuthRepository
import androidx.compose.foundation.clickable

@Composable
fun UserScreen(
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    onLogout: () -> Unit,
    onNavigateToIptv: () -> Unit
) {
    val authRepository = remember { AuthRepository() }
    // Estado para almacenar el usuario cargado
    var user by remember { mutableStateOf<User?>(null) }
    
    // Cargar usuario de forma asíncrona usando LaunchedEffect
    LaunchedEffect(Unit) {
        user = authRepository.getCurrentUser()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Avatar
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
             Icon(
                 imageVector = Icons.Default.Person,
                 contentDescription = "Avatar",
                 modifier = Modifier.padding(24.dp).fillMaxSize(),
                 tint = MaterialTheme.colorScheme.onPrimaryContainer
             )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Info
        val displayName = if (user != null && (user!!.firstName.isNotBlank() || user!!.lastName.isNotBlank())) {
            "${user!!.firstName} ${user!!.lastName}".trim()
        } else {
            user?.email ?: "Cargando..."
        }
        
        Text(
            text = displayName,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        if (user != null && displayName != user!!.email) {
            Text(
                text = user!!.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = "Miembro desde 2024",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Settings Section
        // Settings Section
        var isEditing by remember { mutableStateOf(false) }
        var newFirstName by remember { mutableStateOf(user?.firstName ?: "") }
        var newLastName by remember { mutableStateOf(user?.lastName ?: "") }
        val scope = rememberCoroutineScope()
        val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<com.example.proyectofinal.ui.viewmodel.MoviesViewModel>()

        if (isEditing) {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = newFirstName,
                    onValueChange = { newFirstName = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newLastName,
                    onValueChange = { newLastName = it },
                    label = { Text("Apellido") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { isEditing = false }) {
                        Text("Cancelar")
                    }
                    Button(onClick = {
                        scope.launch {
                            viewModel.updateUserProfile(newFirstName, newLastName)
                            user = authRepository.getCurrentUser() // Refresh local user
                            isEditing = false
                        }
                    }) {
                        Text("Guardar")
                    }
                }
            }
        } else {
            SettingsItem(
                title = "Editar Perfil", 
                onClick = { 
                    isEditing = true 
                    newFirstName = user?.firstName ?: ""
                    newLastName = user?.lastName ?: ""
                }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        Text("Conexiones", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(Alignment.Start))
        SettingsItem(
            title = "Conectar IPTV (Xtream Codes)",
            onClick = onNavigateToIptv
        )
        
        // Theme Toggle
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Modo Oscuro",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = onThemeChanged
                )
            }
        }

        SettingsItem(title = "Ayuda y Soporte")

        Spacer(modifier = Modifier.weight(1f))

        // Logout
        Button(
            onClick = {
                authRepository.logout()
                onLogout()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar Sesión")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SettingsItem(title: String, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title, 
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}