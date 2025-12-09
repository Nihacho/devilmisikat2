package com.example.proyectofinal.data.sensors

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.os.Build
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import com.google.android.gms.location.ActivityRecognitionResult

class ActivityRecognitionHelper(
    private val context: Context,
    private val onActivityDetected: (String, Int) -> Unit
) {

    private val client = ActivityRecognition.getClient(context)
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (ActivityRecognitionResult.hasResult(intent)) {
                val result = ActivityRecognitionResult.extractResult(intent!!)
                result?.mostProbableActivity?.let { activity ->
                    val type = getActivityString(activity.type)
                    onActivityDetected(type, activity.confidence)
                }
            }
        }
    }

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, ActivityReceiver::class.java)
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    fun iniciar() {
        // En una implementación real, se debe registrar el Receiver
        // Aquí simplificamos simulando o conectando directamnete si fuera un servicio
        // Para este proyecto, asumiremos que ActivityReceiver envía un broadcast local
        LocalBroadcastManager.getInstance(context).registerReceiver(
            receiver,
            IntentFilter("ACTIVITY_DETECTED")
        )
        
        client.requestActivityUpdates(1000L, pendingIntent)
            .addOnSuccessListener { /* Éxito */ }
            .addOnFailureListener { /* Error */ }
    }

    fun detener() {
        client.removeActivityUpdates(pendingIntent)
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
    }

    private fun getActivityString(type: Int): String {
        return when (type) {
            DetectedActivity.IN_VEHICLE -> "En vehículo"
            DetectedActivity.ON_BICYCLE -> "En bicicleta"
            DetectedActivity.ON_FOOT -> "A pie"
            DetectedActivity.RUNNING -> "Corriendo"
            DetectedActivity.STILL -> "Quieto"
            DetectedActivity.TILTING -> "Inclinación"
            DetectedActivity.WALKING -> "Caminando"
            else -> "Desconocido"
        }
    }
}

class ActivityReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            // Reenviar a través de LocalBroadcastManager para que la UI lo reciba
            // Nota: En una app de producción esto se maneja mejor con un Service o Flow
            val forwardIntent = Intent("ACTIVITY_DETECTED")
            forwardIntent.putExtras(intent)
            LocalBroadcastManager.getInstance(context).sendBroadcast(forwardIntent)
        }
    }
}