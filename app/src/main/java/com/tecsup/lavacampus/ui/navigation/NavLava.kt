package com.tecsup.lavacampus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.ui.administrador.PanelScreen
import com.tecsup.lavacampus.ui.estudiante.DetallePedidoScreen
import com.tecsup.lavacampus.ui.estudiante.InicioScreen
import com.tecsup.lavacampus.ui.estudiante.LoginScreen
import com.tecsup.lavacampus.ui.estudiante.MisPedidosScreen
import com.tecsup.lavacampus.ui.estudiante.NuevoPedidoScreen
import com.tecsup.lavacampus.ui.estudiante.SesionScreen
import com.tecsup.lavacampus.ui.estudiante.TarifasScreen
import com.tecsup.lavacampus.viewmodel.AuthViewModel
import com.tecsup.lavacampus.viewmodel.DetallePedidoViewModel
import com.tecsup.lavacampus.viewmodel.InicioViewModel
import com.tecsup.lavacampus.viewmodel.LavaViewModelFactory
import com.tecsup.lavacampus.viewmodel.MisPedidosViewModel
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
import com.tecsup.lavacampus.viewmodel.PanelViewModel
import com.tecsup.lavacampus.viewmodel.SesionViewModel

@Composable
fun NavLava(
    repository: LavaRepository,
    preferenciasStore: PreferenciasStore
) {
    val navController = rememberNavController()
    val factory = LavaViewModelFactory(repository, preferenciasStore)

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {
        // 1. Pantalla de Login
        composable(Rutas.LOGIN) {
            val authVm: AuthViewModel = viewModel(factory = factory)
            LoginScreen(
                viewModel = authVm,
                onLoginSuccess = {
                    navController.navigate(Rutas.ADMIN_HOME) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // 2. Panel del Administrador
        composable(Rutas.ADMIN_HOME) {
            val panelVm: PanelViewModel = viewModel(factory = factory)
            PanelScreen(
                vm = panelVm,
                onVerPedidosListos = {
                    navController.navigate(Rutas.BANDEJA)
                }
            )
        }

        // 3. Inicio del Estudiante
        composable(Rutas.INICIO) {
            val inicioVm: InicioViewModel = viewModel(factory = factory)
            InicioScreen(
                inicioViewModel = inicioVm,
                onNuevoPedido = { navController.navigate(Rutas.NUEVO_PEDIDO) },
                onVerDetallePedido = { idPedido ->
                    navController.navigate("${Rutas.DETALLE_PEDIDO}/$idPedido")
                },
                onVerPedidos = { navController.navigate(Rutas.BANDEJA) },
                onVerTarifas = { navController.navigate(Rutas.TARIFAS) }
            )
        }

        // 4. Mis Pedidos / Bandeja
        composable(Rutas.BANDEJA) {
            val misPedidosVm: MisPedidosViewModel = viewModel(factory = factory)
            MisPedidosScreen(
                viewModel = misPedidosVm,
                onPedidoClick = { idPedido ->
                    navController.navigate("${Rutas.DETALLE_PEDIDO}/$idPedido")
                }
            )
        }

        // 5. Nuevo Pedido
        composable(Rutas.NUEVO_PEDIDO) {
            val nuevoPedidoVm: NuevoPedidoViewModel = viewModel(factory = factory)
            NuevoPedidoScreen(
                nuevoPedidoViewModel = nuevoPedidoVm,
                onPedidoCreado = { navController.popBackStack() },
                onVolver = { navController.popBackStack() }
            )
        }

        // 6. Detalle del Pedido
        composable(
            route = "${Rutas.DETALLE_PEDIDO}/{pedidoId}",
            arguments = listOf(navArgument("pedidoId") { type = NavType.IntType })
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getInt("pedidoId") ?: 0
            val detalleVm: DetallePedidoViewModel = viewModel(factory = factory)
            DetallePedidoScreen(
                pedidoId = pedidoId,
                detalleViewModel = detalleVm,
                onVolver = { navController.popBackStack() }
            )
        }

        // 7. Tarifas
        composable(Rutas.TARIFAS) {
            val misPedidosVm: MisPedidosViewModel = viewModel(factory = factory)
            TarifasScreen(
                viewModel = misPedidosVm
            )
        }

        // 8. Sesión / Perfil
        composable(Rutas.SESION) {
            val sesionVm: SesionViewModel = viewModel(factory = factory)
            SesionScreen(
                viewModel = sesionVm,
                onCerrarSesion = {
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}