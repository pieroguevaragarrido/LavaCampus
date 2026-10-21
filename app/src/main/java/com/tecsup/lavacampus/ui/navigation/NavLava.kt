package com.tecsup.lavacampus.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.lavacampus.ui.estudiante.InicioScreen
import com.tecsup.lavacampus.ui.estudiante.LoginScreen
import com.tecsup.lavacampus.ui.estudiante.NuevoPedidoScreen
import com.tecsup.lavacampus.viewmodel.InicioViewModel
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
import com.tecsup.lavacampus.viewmodel.SesionViewModel

sealed class Destino(val ruta: String) {
    object Login : Destino("login")
    object EstudianteHome : Destino("estudiante_home")
    object NuevoPedido : Destino("nuevo_pedido")
    object AdminHome : Destino("admin_home")
}

@Composable
fun NavLava(
    navController: NavHostController = rememberNavController(),
    sesionViewModel: SesionViewModel,
    inicioViewModel: InicioViewModel,
    nuevoPedidoViewModel: NuevoPedidoViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Destino.Login.ruta
    ) {
        composable(Destino.Login.ruta) {
            LoginScreen(
                sesionViewModel = sesionViewModel,
                onLoginSuccess = { rol ->
                    val destino = if (rol == "ADMIN") Destino.AdminHome.ruta else Destino.EstudianteHome.ruta
                    navController.navigate(destino) {
                        popUpTo(Destino.Login.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Destino.EstudianteHome.ruta) {
            InicioScreen(
                inicioViewModel = inicioViewModel,
                onNuevoPedido = { navController.navigate(Destino.NuevoPedido.ruta) },
                onVerPedidos = { },
                onVerTarifas = { },
                onVerDetallePedido = { }
            )
        }

        composable(Destino.NuevoPedido.ruta) {
            NuevoPedidoScreen(
                nuevoPedidoViewModel = nuevoPedidoViewModel,
                onVolver = { navController.popBackStack() },
                onPedidoCreado = { navController.popBackStack() }
            )
        }

        composable(Destino.AdminHome.ruta) {
            Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFFAF8FF)) {
                Box(contentAlignment = Alignment.Center) {
                    Text("Panel del Administrador (Paso 11)")
                }
            }
        }
    }
}