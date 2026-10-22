package com.tecsup.lavacampus.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class PreferenciasStore(private val context: Context) {

    companion object {
        val KEY_USUARIO_ID = intPreferencesKey("usuario_id")
        val KEY_NOMBRE = stringPreferencesKey("nombre")
        val KEY_CORREO = stringPreferencesKey("correo")
        val KEY_ROL = stringPreferencesKey("rol")
        val KEY_MODO_OSCURO = booleanPreferencesKey("modo_oscuro")
    }

    val usuarioId: Flow<Int?> = context.dataStore.data.map { prefs -> prefs[KEY_USUARIO_ID] }
    val nombre: Flow<String?> = context.dataStore.data.map { prefs -> prefs[KEY_NOMBRE] }
    val correo: Flow<String?> = context.dataStore.data.map { prefs -> prefs[KEY_CORREO] }
    val rol: Flow<String?> = context.dataStore.data.map { prefs -> prefs[KEY_ROL] }
    val modoOscuro: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[KEY_MODO_OSCURO] ?: false }

    suspend fun guardarSesion(
        id: Int = 0,
        usuarioId: Int = id,
        nombre: String,
        correo: String,
        rol: String
    ) {
        val finalId = if (usuarioId != 0) usuarioId else id
        context.dataStore.edit { prefs ->
            prefs[KEY_USUARIO_ID] = finalId
            prefs[KEY_NOMBRE] = nombre
            prefs[KEY_CORREO] = correo
            prefs[KEY_ROL] = rol
        }
    }

    suspend fun guardarModoOscuro(activo: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_MODO_OSCURO] = activo
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}