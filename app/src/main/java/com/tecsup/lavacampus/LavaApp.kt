package com.tecsup.lavacampus

import android.app.Application
import com.tecsup.lavacampus.data.local.DatosIniciales
import com.tecsup.lavacampus.data.local.LavaDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LavaApp : Application() {

    lateinit var database: LavaDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = LavaDatabase.obtener(this)
        CoroutineScope(Dispatchers.IO).launch {
            DatosIniciales.sembrar(database)
        }
    }
}