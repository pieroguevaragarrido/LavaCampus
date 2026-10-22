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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.lavacampus.ui.estudiante.DetallePedidoScreen
import com.tecsup.lavacampus.ui.estudiante.InicioScreen
import com.tecsup.lavacampus.ui.estudiante.LoginScreen
import com.tecsup.lavacampus.ui.estudiante.NuevoPedidoScreen
import com.tecsup.lavacampus.viewmodel.DetallePedidoViewModel
import com.tecsup.lavacampus.viewmodel.InicioViewModel
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
import com.tecsup.lavacampus.viewmodel.SesionViewModel

sealed class Destino(val ruta: String) {
    object Login : Destino("login")
    object EstudianteHome : Destino("estudiante_home")
    object NuevoPedido : Destino("nuevo_pedido")
    object DetallePedido : Destino("detalle_pedido/{pedidoId}") {
        fun crearRuta(pedidoId: Int) = "detalle_pedido/$pedidoId"
    }
    object AdminHome : Destino("admin_home")
}

@Composable
fun NavLava(
    navController: NavHostController = rememberNavController(),
    sesionViewModel: SesionViewModel,
    inicioViewModel: InicioViewModel,
    nuevoPedidoViewModel: NuevoPedidoViewModel,
    detalleViewModel: DetallePedidoViewModel
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
                onVerDetallePedido = { pedidoId ->
                    navController.navigate(Destino.DetallePedido.crearRuta(pedidoId))
                }
            )
        }

        composable(Destino.NuevoPedido.ruta) {
            NuevoPedidoScreen(
                nuevoPedidoViewModel = nuevoPedidoViewModel,
                onVolver = { navController.popBackStack() },
                onPedidoCreado = { navController.popBackStack() }
            )
        }

        composable(
            route = Destino.DetallePedido.ruta,
            arguments = listOf(navArgument("pedidoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getInt("pedidoId") ?: 0
            DetallePedidoScreen(
                pedidoId = pedidoId,
                detalleViewModel = detalleViewModel,
                onVolver = { navController.popBackStack() }
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