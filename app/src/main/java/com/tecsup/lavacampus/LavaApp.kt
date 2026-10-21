package com.tecsup.lavacampus

import android.app.Application
import com.tecsup.lavacampus.data.local.DatosIniciales
import com.tecsup.lavacampus.data.local.LavaDatabase
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.data.repository.LavaRepositoryRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LavaApp : Application() {

    lateinit var database: LavaDatabase
        private set

    lateinit var repository: LavaRepository
        private set

    lateinit var preferenciasStore: PreferenciasStore
        private set

    override fun onCreate() {
        super.onCreate()
        database = LavaDatabase.obtener(this)
        repository = LavaRepositoryRoom(database)
        preferenciasStore = PreferenciasStore(this)

        CoroutineScope(Dispatchers.IO).launch {
            DatosIniciales.sembrar(database)
        }
    }
}