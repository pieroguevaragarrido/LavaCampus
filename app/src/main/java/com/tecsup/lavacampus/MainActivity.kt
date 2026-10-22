package com.tecsup.lavacampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.tecsup.lavacampus.ui.navigation.NavLava
import com.tecsup.lavacampus.ui.theme.LavaCampusTheme
import com.tecsup.lavacampus.viewmodel.DetallePedidoViewModel
import com.tecsup.lavacampus.viewmodel.InicioViewModel
import com.tecsup.lavacampus.viewmodel.LavaViewModelFactory
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
import com.tecsup.lavacampus.viewmodel.SesionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as LavaApp
        val factory = LavaViewModelFactory(app.repository, app.preferenciasStore)
        val sesionViewModel = ViewModelProvider(this, factory)[SesionViewModel::class.java]
        val inicioViewModel = ViewModelProvider(this, factory)[InicioViewModel::class.java]
        val nuevoPedidoViewModel = ViewModelProvider(this, factory)[NuevoPedidoViewModel::class.java]
        val detalleViewModel = ViewModelProvider(this, factory)[DetallePedidoViewModel::class.java]

        setContent {
            LavaCampusTheme {
                NavLava(
                    sesionViewModel = sesionViewModel,
                    inicioViewModel = inicioViewModel,
                    nuevoPedidoViewModel = nuevoPedidoViewModel,
                    detalleViewModel = detalleViewModel
                )
            }
        }
    }
}