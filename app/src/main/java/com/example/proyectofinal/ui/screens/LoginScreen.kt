package com.example.proyectofinal.ui.screens

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.proyectofinal.data.model.User
import com.example.proyectofinal.data.model.UserRole
import com.example.proyectofinal.data.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (UserRole) -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    // Estado para alternar entre Login (true) y Registro (false)
    var isLoginMode by remember { mutableStateOf(true) }

    // Estados para campos de entrada
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }
    val primaryColor = Color(0xFFCC0000) // Rojo oscuro (Devil)
    val tertiaryColor = Color(0xFF000000) // Negro

    // Reiniciar campos al cambiar de modo
    LaunchedEffect(isLoginMode) {
        email = ""
        password = ""
        confirmPassword = ""
        firstName = ""
        lastName = ""
        errorMessage = null
    }
    
    // Manejar botón atrás para volver a Login si está en Registro
    BackHandler(!isLoginMode) {
        isLoginMode = true
    }

    // Lógica reutilizable para éxito y error
    val handleSuccess: (User) -> Unit = { user ->
        isLoading = false
        Log.d("LoginScreen", "Operación Exitosa: ${user.email}")
        onLoginSuccess(user.role)
    }

    val handleError: (String) -> Unit = { error ->
        isLoading = false
        errorMessage = error
        Log.e("LoginScreen", "Error: $error")
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
    }

    // Google Sign In Launcher
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account?.idToken?.let { idToken ->
                    val credential = GoogleAuthProvider.getCredential(idToken, null)
                    isLoading = true
                    viewModel.loginWithCredential(
                        credential = credential,
                        authRepository = authRepository,
                        onSuccess = handleSuccess,
                        onError = handleError
                    )
                }
            } catch (e: ApiException) {
                handleError("Google Sign In Falló: ${e.message}")
            }
        } else {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = Color.White
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
                    .height(240.dp) 
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(width, 0f)
                        lineTo(width, height * 0.6f) 
                        cubicTo(
                            width * 0.7f, height * 0.55f, 
                            width * 0.35f, height * 1.05f,  
                            0f, height * 0.75f            
                        )
                        lineTo(0f, 0f) 
                        close()
                    }

                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                primaryColor,
                                Color(0xFF8B0000)
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
                Spacer(modifier = Modifier.height(100.dp))

                // Título dinámico
                Text(
                    text = if (isLoginMode) "Sign In" else "Sign Up",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    ),
                    color = if (isLoginMode) Color.Black else Color.White // Contraste con fondo
                )

                Spacer(modifier = Modifier.height(if (isLoginMode) 60.dp else 40.dp))

                if (isLoginMode) {
                    // ---- MODO LOGIN ----
                    Surface(
                        shape = CircleShape,
                        shadowElevation = 4.dp,
                        color = Color(0xFFE0E0E0),
                        modifier = Modifier.size(80.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "User Avatar",
                            tint = Color.Gray,
                            modifier = Modifier.fillMaxSize().padding(8.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    CustomTextField(value = email, onValueChange = { email = it }, placeholder = "Email")
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomTextField(value = password, onValueChange = { password = it }, placeholder = "Password", isPassword = true)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        Text("Forgot your password?", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.clickable {})
                    }
                } else {
                    // ---- MODO REGISTRO ----
                    // Mover formulario más abajo para llenar el espacio vacío de los iconos
                    Spacer(modifier = Modifier.height(20.dp)) 
                    
                    CustomTextField(value = email, onValueChange = { email = it }, placeholder = "Email")
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.weight(1f)) {
                            CustomTextField(value = firstName, onValueChange = { firstName = it }, placeholder = "First Name")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            CustomTextField(value = lastName, onValueChange = { lastName = it }, placeholder = "Last Name")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomTextField(value = password, onValueChange = { password = it }, placeholder = "Password", isPassword = true)
                    Spacer(modifier = Modifier.height(16.dp))
                    CustomTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, placeholder = "Confirm Password", isPassword = true)
                    
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Mensaje de Error
                if (errorMessage != null) {
                    Text(errorMessage!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(bottom = 8.dp))
                }

                // Botón Principal (Login o Sign Up)
                Button(
                    onClick = {
                        isLoading = true
                        errorMessage = null
                        if (isLoginMode) {
                            viewModel.login(email, password, authRepository, handleSuccess, handleError)
                        } else {
                            if (password == confirmPassword) {
                                if (firstName.isNotBlank() && lastName.isNotBlank()) {
                                    viewModel.register(email, password, firstName, lastName, authRepository, handleSuccess, handleError)
                                } else {
                                    handleError("Por favor completa tu nombre y apellido")
                                }
                            } else {
                                handleError("Las contraseñas no coinciden")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .shadow(4.dp, RoundedCornerShape(25.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(25.dp),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(if (isLoginMode) "Sign In" else "Sign Up", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botones Sociales
                // Solo mostramos esto si estamos en modo LOGIN
                AnimatedVisibility(visible = isLoginMode) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            HorizontalDivider(modifier = Modifier.weight(1f))
                            Text(" Or ", color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                            HorizontalDivider(modifier = Modifier.weight(1f))
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                            // Google (Logo Oficial)
                            SocialButton(
                                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/c/c1/Google_%22G%22_logo.svg/120px-Google_%22G%22_logo.svg.png",
                                contentDescription = "Sign in with Google",
                                onClick = {
                                    try {
                                        val clientId = context.getString(
                                            context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                                        )
                                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                            .requestIdToken(clientId)
                                            .requestEmail()
                                            .build()
                                        val googleSignInClient = GoogleSignIn.getClient(context, gso)
                                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                                    } catch (e: Exception) {
                                        handleError("Error configuración Google: ${e.message}")
                                    }
                                }
                            )
                            
                            Spacer(modifier = Modifier.width(24.dp))
                            
                            // GitHub (Logo Oficial)
                            SocialButton(
                                imageUrl = "https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png",
                                contentDescription = "Sign in with GitHub",
                                onClick = {
                                    val provider = OAuthProvider.newBuilder("github.com")
                                    val activity = context as? Activity ?: return@SocialButton
                                    isLoading = true
                                    authRepository.auth.startActivityForSignInWithProvider(activity, provider.build())
                                        .addOnSuccessListener { viewModel.finalizeSocialLogin(authRepository, handleSuccess, handleError) }
                                        .addOnFailureListener { e -> 
                                            val msg = if (e.message?.contains("package certificate hash") == true) "Error SHA-1 en Firebase" else "Error GitHub: ${e.message}"
                                            handleError(msg) 
                                        }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Toggle Mode Text
                Row(modifier = Modifier.padding(bottom = 24.dp)) {
                    Text(if (isLoginMode) "Don't have an account? " else "Have an account? ", color = Color.Gray, fontSize = 14.sp)
                    Text(
                        if (isLoginMode) "Sign Up" else "Sign In",
                        color = primaryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { 
                            isLoginMode = !isLoginMode 
                            errorMessage = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String, 
    onValueChange: (String) -> Unit, 
    placeholder: String, 
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.LightGray) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF5F5F5),
            unfocusedContainerColor = Color(0xFFF5F5F5),
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
fun SocialButton(imageUrl: String, contentDescription: String, onClick: () -> Unit) {
    // Botones más grandes (60dp en lugar de 50dp)
    Surface(
        shape = RoundedCornerShape(16.dp), // Bordes un poco más redondeados
        shadowElevation = 4.dp,
        color = Color.White,
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        modifier = Modifier
            .size(60.dp) // AUMENTO DE TAMAÑO
            .clickable(onClick = onClick)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(12.dp) // Padding ajustado para que el logo se vea bien
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

class LoginViewModel : ViewModel() {
    fun login(email: String, pass: String, authRepository: AuthRepository, onSuccess: (User) -> Unit, onError: (String) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            authRepository.login(email, pass).onSuccess(onSuccess).onFailure { onError(it.message ?: "Error") }
        }
    }

    fun loginWithCredential(credential: AuthCredential, authRepository: AuthRepository, onSuccess: (User) -> Unit, onError: (String) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            authRepository.signInWithCredential(credential).onSuccess(onSuccess).onFailure { onError(it.message ?: "Error") }
        }
    }
    
    fun finalizeSocialLogin(authRepository: AuthRepository, onSuccess: (User) -> Unit, onError: (String) -> Unit) {
         CoroutineScope(Dispatchers.Main).launch {
             val user = authRepository.getCurrentUser()
             if (user != null) onSuccess(user) else onError("Usuario no encontrado")
         }
    }

    fun register(email: String, pass: String, firstName: String, lastName: String, authRepository: AuthRepository, onSuccess: (User) -> Unit, onError: (String) -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            authRepository.register(email, pass, firstName, lastName).onSuccess(onSuccess).onFailure { onError(it.message ?: "Error") }
        }
    }
}
