package com.example.proyectofinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.proyectofinal.ui.navigation.AppNavigation
import com.example.proyectofinal.ui.theme.ProyectofinalTheme

class MainActivity : ComponentActivity() {

    // Se comio la carne se comio el pesacado nada nada le ha llenado. nasehi


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProyectofinalTheme {
                AppNavigation()
            }
        }
    }
}