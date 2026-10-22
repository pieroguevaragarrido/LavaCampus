package com.tecsup.lavacampus

import android.app.Application
import com.tecsup.lavacampus.data.local.LavaDatabase
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository

class LavaApp : Application() {

    val database: LavaDatabase by lazy {
        LavaDatabase.obtenerBaseDatos(this)
    }

    val preferenciasStore: PreferenciasStore by lazy {
        PreferenciasStore(this)
    }

    val repository: LavaRepository by lazy {
        LavaRepository(
            usuarioDao = database.usuarioDao(),
            servicioDao = database.servicioDao(),
            pedidoDao = database.pedidoDao(),
            detalleDao = database.detalleDao()
        )
    }
}