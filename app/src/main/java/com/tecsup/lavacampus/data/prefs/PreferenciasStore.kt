package com.tecsup.lavacampus.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lavacampus_prefs")

class PreferenciasStore(private val context: Context) {

    companion object {
        private val KEY_USUARIO_ID = intPreferencesKey("usuario_id")
        private val KEY_NOMBRE = stringPreferencesKey("nombre")
        private val KEY_CORREO = stringPreferencesKey("correo")
        private val KEY_ROL = stringPreferencesKey("rol")
        private val KEY_MODO_OSCURO = booleanPreferencesKey("modo_oscuro")
    }

    val usuarioId: Flow<Int?> = context.dataStore.data.map { prefs ->
        val id = prefs[KEY_USUARIO_ID]
        if (id != null && id > 0) id else null
    }

    val rol: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_ROL]
    }

    val nombre: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_NOMBRE]
    }

    val correo: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_CORREO]
    }

    val modoOscuro: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_MODO_OSCURO] ?: false
    }

    suspend fun guardarSesion(id: Int, nombre: String, correo: String, rol: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USUARIO_ID] = id
            prefs[KEY_NOMBRE] = nombre
            prefs[KEY_CORREO] = correo
            prefs[KEY_ROL] = rol
        }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_USUARIO_ID)
            prefs.remove(KEY_NOMBRE)
            prefs.remove(KEY_CORREO)
            prefs.remove(KEY_ROL)
        }
    }

    suspend fun guardarModoOscuro(activo: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_MODO_OSCURO] = activo
        }
    }
}