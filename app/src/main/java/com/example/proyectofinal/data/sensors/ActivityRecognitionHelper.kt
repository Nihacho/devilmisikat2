package com.example.proyectofinal.data.sensors

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity

class ActivityRecognitionHelper(
    private val context: Context,
    private val onActivityDetected: (String, Int) -> Unit
) {

    companion object {
        private const val ACTION_ACTIVITY_DETECTION = "com.example.proyectofinal.ACTIVITY_DETECTION"
    }

    private var isReceiverRegistered = false

    private val activityReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (intent?.action != ACTION_ACTIVITY_DETECTION) return

            if (ActivityRecognitionResult.hasResult(intent)) {
                val result = ActivityRecognitionResult.extractResult(intent)
                result?.probableActivities?.let { activities ->
                    handleDetectedActivities(activities)
                }
            }
        }
    }

    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(ACTION_ACTIVITY_DETECTION).apply {
            setPackage(context.packageName)
        }

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        PendingIntent.getBroadcast(context, 0, intent, flags)
    }

    fun iniciar() {
        if (isReceiverRegistered) {
            try {
                context.unregisterReceiver(activityReceiver)
                isReceiverRegistered = false
                Log.d("ActivityRecognition", "🔄 Desregistrado antes de re-registrar")
            } catch (e: Exception) {
                Log.e("ActivityRecognition", "⚠️ Error al desregistrar: ${e.message}")
            }
        }

        try {
            val filter = IntentFilter(ACTION_ACTIVITY_DETECTION)

            ContextCompat.registerReceiver(
                context,
                activityReceiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )

            isReceiverRegistered = true
            Log.d("ActivityRecognition", "✅ Receiver registrado correctamente")
        } catch (e: Exception) {
            Log.e("ActivityRecognition", "❌ Error al registrar receiver: ${e.message}")
            return
        }

        try {
            ActivityRecognition.getClient(context)
                .requestActivityUpdates(2000, pendingIntent)
                .addOnSuccessListener {
                    Log.d("ActivityRecognition", "✅ Actualizaciones iniciadas")
                }
                .addOnFailureListener { e ->
                    Log.e("ActivityRecognition", "❌ Error al iniciar: ${e.message}")
                }
        } catch (e: SecurityException) {
            Log.e("ActivityRecognition", "❌ SecurityException: ${e.message}")
        }
    }

    fun detener() {
        try {
            try {
                ActivityRecognition.getClient(context)
                    .removeActivityUpdates(pendingIntent)
            } catch (e: SecurityException) {
                Log.e("ActivityRecognition", "SecurityException al remover: ${e.message}")
            }

            if (isReceiverRegistered) {
                context.unregisterReceiver(activityReceiver)
                isReceiverRegistered = false
            }

            Log.d("ActivityRecognition", "✅ Detenido correctamente")
        } catch (e: Exception) {
            Log.e("ActivityRecognition", "❌ Error al detener: ${e.message}")
        }
    }

    private fun handleDetectedActivities(activities: List<DetectedActivity>) {
        val bestActivity = activities.maxByOrNull { it.confidence }
        bestActivity?.let { activity ->
            val name = getActivityName(activity.type)
            val confidence = activity.confidence
            onActivityDetected(name, confidence)
            Log.d("ActivityRecognition", "Detectado: $name ($confidence%)")
        }
    }

    private fun getActivityName(type: Int): String {
        return when (type) {
            DetectedActivity.STILL -> "Quieto 🧍"
            DetectedActivity.WALKING -> "Caminando 🚶"
            DetectedActivity.RUNNING -> "Corriendo 🏃"
            DetectedActivity.ON_BICYCLE -> "Bicicleta 🚴"
            DetectedActivity.IN_VEHICLE -> "Vehículo 🚗"
            DetectedActivity.ON_FOOT -> "A Pie 👟"
            DetectedActivity.TILTING -> "Inclinando 📐"
            else -> "Desconocido ❓"
        }
    }
}