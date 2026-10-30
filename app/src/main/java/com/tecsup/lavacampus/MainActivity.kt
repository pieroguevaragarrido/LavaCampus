package com.tecsup.lavacampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.tecsup.lavacampus.ui.navigation.NavLava
import com.tecsup.lavacampus.ui.theme.LavaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as LavaApp

        setContent {
            val oscuro by app.preferenciasStore.modoOscuro.collectAsState(initial = false)
            val rol by app.preferenciasStore.rol.collectAsState(initial = null)

            LavaTheme(rol = rol, oscuro = oscuro) {
                NavLava(app = app)
            }
        }
    }
}