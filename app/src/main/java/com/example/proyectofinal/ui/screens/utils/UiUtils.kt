package com.example.proyectofinal.ui.screens.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Encuentra la Activity principal desde un Contexto (necesario para cambiar la orientación).
 */
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * Diálogo para agregar una película a una de las listas del usuario.
 */
@Composable
fun AddToListDialog(
    lists: List<String>,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar a mi lista") },
        text = {
            LazyColumn {
                items(lists.size) { index ->
                    val listName = lists[index]
                    TextButton(
                        onClick = { onSelect(listName) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(listName)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
