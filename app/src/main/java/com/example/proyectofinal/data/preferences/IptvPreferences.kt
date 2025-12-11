package com.example.proyectofinal.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension for DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "iptv_preferences")

class IptvPreferences(private val context: Context) {
    
    companion object {
        private val IPTV_SERVER_URL = stringPreferencesKey("iptv_server_url")
        private val IPTV_USERNAME = stringPreferencesKey("iptv_username")
        private val IPTV_PASSWORD = stringPreferencesKey("iptv_password")
        private val IPTV_IS_LOGGED_IN = booleanPreferencesKey("iptv_is_logged_in")
    }

    // Guardar credenciales
    suspend fun saveIptvCredentials(serverUrl: String, username: String, password: String) {
        context.dataStore.edit { preferences ->
            preferences[IPTV_SERVER_URL] = serverUrl
            preferences[IPTV_USERNAME] = username
            preferences[IPTV_PASSWORD] = password
            preferences[IPTV_IS_LOGGED_IN] = true
        }
    }

    // Obtener credenciales
    val serverUrl: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[IPTV_SERVER_URL] ?: ""
    }

    val username: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[IPTV_USERNAME] ?: ""
    }

    val password: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[IPTV_PASSWORD] ?: ""
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IPTV_IS_LOGGED_IN] ?: false
    }

    // Obtener todas las credenciales de una vez
    data class IptvCredentials(
        val serverUrl: String,
        val username: String,
        val password: String,
        val isLoggedIn: Boolean
    )

    val credentials: Flow<IptvCredentials> = context.dataStore.data.map { preferences ->
        IptvCredentials(
            serverUrl = preferences[IPTV_SERVER_URL] ?: "",
            username = preferences[IPTV_USERNAME] ?: "",
            password = preferences[IPTV_PASSWORD] ?: "",
            isLoggedIn = preferences[IPTV_IS_LOGGED_IN] ?: false
        )
    }

    // Limpiar credenciales (logout)
    suspend fun clearCredentials() {
        context.dataStore.edit { preferences ->
            preferences.remove(IPTV_SERVER_URL)
            preferences.remove(IPTV_USERNAME)
            preferences.remove(IPTV_PASSWORD)
            preferences[IPTV_IS_LOGGED_IN] = false
        }
    }
}
