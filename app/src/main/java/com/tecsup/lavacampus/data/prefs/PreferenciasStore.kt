package com.tecsup.lavacampus.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class PreferenciasStore(private val context: Context) {

    companion object {
        val KEY_USUARIO_ID = intPreferencesKey("usuario_id")
        val KEY_NOMBRE = stringPreferencesKey("nombre")
        val KEY_CORREO = stringPreferencesKey("correo")
        val KEY_CODIGO = stringPreferencesKey("codigo")
        val KEY_ROL = stringPreferencesKey("rol")
        val KEY_ULTIMO_CLIENTE = intPreferencesKey("ultimo_cliente_id")
        val KEY_MODO_OSCURO = booleanPreferencesKey("modo_oscuro")
        val KEY_NOTIFICACIONES = booleanPreferencesKey("notificaciones")
        val KEY_RECIBIR_PEDIDOS = booleanPreferencesKey("recibir_pedidos")
    }

    val usuarioId: Flow<Int?> = context.dataStore.data.map { prefs -> prefs[KEY_USUARIO_ID] }
    val nombre: Flow<String?> = context.dataStore.data.map { prefs -> prefs[KEY_NOMBRE] }
    val correo: Flow<String?> = context.dataStore.data.map { prefs -> prefs[KEY_CORREO] }
    val codigo: Flow<String?> = context.dataStore.data.map { prefs -> prefs[KEY_CODIGO] }
    val rol: Flow<String?> = context.dataStore.data.map { prefs -> prefs[KEY_ROL] }
    val ultimoCliente: Flow<Int?> = context.dataStore.data.map { prefs -> prefs[KEY_ULTIMO_CLIENTE] }
    val modoOscuro: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[KEY_MODO_OSCURO] ?: false }
    val notificaciones: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[KEY_NOTIFICACIONES] ?: true }
    val recibirPedidos: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[KEY_RECIBIR_PEDIDOS] ?: true }

    suspend fun guardarSesion(
        id: Int = 0,
        usuarioId: Int = id,
        nombre: String,
        correo: String,
        rol: String,
        codigo: String = ""
    ) {
        val finalId = if (usuarioId != 0) usuarioId else id
        context.dataStore.edit { prefs ->
            prefs[KEY_USUARIO_ID] = finalId
            prefs[KEY_NOMBRE] = nombre
            prefs[KEY_CORREO] = correo
            prefs[KEY_CODIGO] = codigo
            prefs[KEY_ROL] = rol
            if (rol == "CLIENTE") {
                prefs[KEY_ULTIMO_CLIENTE] = finalId
            }
        }
    }

    suspend fun guardarModoOscuro(activo: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_MODO_OSCURO] = activo }
    }

    suspend fun guardarNotificaciones(activo: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_NOTIFICACIONES] = activo }
    }

    suspend fun guardarRecibirPedidos(activo: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_RECIBIR_PEDIDOS] = activo }
    }

    /** Borra solo la sesión; el modo oscuro y los ajustes se conservan. */
    suspend fun cerrarSesion() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_USUARIO_ID)
            prefs.remove(KEY_NOMBRE)
            prefs.remove(KEY_CORREO)
            prefs.remove(KEY_CODIGO)
            prefs.remove(KEY_ROL)
            prefs.remove(KEY_ULTIMO_CLIENTE)
        }
    }
}