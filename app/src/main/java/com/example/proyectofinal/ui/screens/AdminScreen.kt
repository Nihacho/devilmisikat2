package com.example.proyectofinal.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.proyectofinal.data.repository.AuthRepository
import com.example.proyectofinal.data.sensors.ActivityRecognitionHelper
import com.google.firebase.auth.FirebaseAuth
import kotlin.math.sqrt


private val auth: FirebaseAuth = FirebaseAuth.getInstance()
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }

    // Estados para sensores
    var estadoMovimiento by remember { mutableStateOf("Calibrando...") }
    var intensidadMovimiento by remember { mutableFloatStateOf(0f) }
    var tipoMovimiento by remember { mutableStateOf("") }
    var calibracionCompleta by remember { mutableStateOf(false) }

    // Estados para Google API
    var actividadGoogle by remember { mutableStateOf("Esperando...") }
    var confianzaGoogle by remember { mutableIntStateOf(0) }
    var googleActivo by remember { mutableStateOf(false) }

    // Sensor Manager
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    val acelerometro = remember { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }

    // Activity Helper
    val activityHelper = remember {
        ActivityRecognitionHelper(context) { actividad, confianza ->
            actividadGoogle = actividad
            confianzaGoogle = confianza
        }
    }

    // Sensor Listener
    val sensorListener = remember {
        object : SensorEventListener {
            val historialAceleracion = ArrayDeque<Float>(15)
            val muestrasCalibracion = mutableListOf<Float>()
            val muestrasRequeridas = 30

            override fun onSensorChanged(event: SensorEvent?) {
                event ?: return

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val magnitud = sqrt(x * x + y * y + z * z)

                if (!calibracionCompleta) {
                    muestrasCalibracion.add(magnitud)
                    if (muestrasCalibracion.size >= muestrasRequeridas) {
                        calibracionCompleta = true
                        estadoMovimiento = "✅ Listo"
                        tipoMovimiento = "Sistema calibrado"
                    } else {
                        val progreso = (muestrasCalibracion.size * 100) / muestrasRequeridas
                        estadoMovimiento = "Calibrando... $progreso%"
                    }
                } else {
                    val movimiento = (magnitud - 9.81f).coerceAtLeast(0f)

                    if (historialAceleracion.size >= 15) {
                        historialAceleracion.removeFirst()
                    }
                    historialAceleracion.addLast(movimiento)

                    val promedio = historialAceleracion.average().toFloat()
                    intensidadMovimiento = promedio

                    estadoMovimiento = when {
                        promedio > 2.5f -> "🔴 Fuerte"
                        promedio > 1.2f -> "🟡 Moderado"
                        promedio > 0.3f -> "🟢 Suave"
                        else -> "⚪ Quieto"
                    }

                    tipoMovimiento = when {
                        promedio > 2.5f -> "Movimiento fuerte"
                        promedio > 1.2f -> "Caminando"
                        promedio > 0.3f -> "Movimiento ligero"
                        else -> "Sin movimiento"
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }
    }

    // Launcher para pedir permisos
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            activityHelper.iniciar()
            googleActivo = true
            actividadGoogle = "Esperando detección..."
        } else {
            actividadGoogle = "Permiso denegado"
        }
    }

    // Función para toggle Google API
    fun toggleGoogle() {
        if (googleActivo) {
            activityHelper.detener()
            googleActivo = false
            actividadGoogle = "Desactivado"
            confianzaGoogle = 0
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                when (ContextCompat.checkSelfPermission(context, Manifest.permission.ACTIVITY_RECOGNITION)) {
                    PackageManager.PERMISSION_GRANTED -> {
                        activityHelper.iniciar()
                        googleActivo = true
                        actividadGoogle = "Esperando detección..."
                    }
                    else -> permissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
                }
            } else {
                activityHelper.iniciar()
                googleActivo = true
            }
        }
    }

    // Registrar/desregistrar sensores
    DisposableEffect(Unit) {
        acelerometro?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_GAME)
        }

        onDispose {
            sensorManager.unregisterListener(sensorListener)
            if (googleActivo) {
                activityHelper.detener()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin - Sensores de Movimiento") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    TextButton(
                        onClick = {
                            // Se llama a la función del repositorio y luego al callback de navegación
                            authRepository.logout()
                            onLogout()
                        }
                    ) {
                        Text("Cerrar Sesión")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Comparación de Métodos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // Card: Tu Algoritmo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF42A5F5))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TU ALGORITMO",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF42A5F5)
                        )
                    }

                    if (!calibracionCompleta) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Text(
                        text = estadoMovimiento,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = tipoMovimiento,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    if (calibracionCompleta) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Intensidad: ${String.format("%.2f", intensidadMovimiento)} m/s²",
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { (intensidadMovimiento / 6f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = when {
                                intensidadMovimiento > 2.5f -> Color(0xFFFF5252)
                                intensidadMovimiento > 1.2f -> Color(0xFFFFD740)
                                intensidadMovimiento > 0.3f -> Color(0xFF69F0AE)
                                else -> Color.Gray
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Card: Google API
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFF4CAF50))
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "GOOGLE API",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                        Switch(
                            checked = googleActivo,
                            onCheckedChange = { toggleGoogle() }
                        )
                    }

                    Text(
                        text = actividadGoogle,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (googleActivo && confianzaGoogle > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Confianza: $confianzaGoogle%",
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { confianzaGoogle / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = Color(0xFF4CAF50)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "💡 Compara ambos métodos en tiempo real",
                    fontSize = 13.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

private fun AuthRepository.logout() {

    auth.signOut()
}



