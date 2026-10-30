package com.tecsup.lavacampus.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.lavacampus.LavaApp
import com.tecsup.lavacampus.ui.administrador.AjustesScreen
import com.tecsup.lavacampus.ui.administrador.BandejaScreen
import com.tecsup.lavacampus.ui.administrador.ClientesScreen
import com.tecsup.lavacampus.ui.administrador.FormularioServicioScreen
import com.tecsup.lavacampus.ui.administrador.GestionPedidoScreen
import com.tecsup.lavacampus.ui.administrador.PanelScreen
import com.tecsup.lavacampus.ui.administrador.ServiciosScreen
import com.tecsup.lavacampus.ui.estudiante.DetallePedidoScreen
import com.tecsup.lavacampus.ui.estudiante.InicioScreen
import com.tecsup.lavacampus.ui.estudiante.LoginScreen
import com.tecsup.lavacampus.ui.estudiante.MisPedidosScreen
import com.tecsup.lavacampus.ui.estudiante.NuevoPedidoScreen
import com.tecsup.lavacampus.ui.estudiante.PerfilScreen
import com.tecsup.lavacampus.ui.estudiante.TarifasScreen
import com.tecsup.lavacampus.ui.theme.PantallaClara
import com.tecsup.lavacampus.viewmodel.AuthViewModel
import com.tecsup.lavacampus.viewmodel.BandejaViewModel
import com.tecsup.lavacampus.viewmodel.ClientesViewModel
import com.tecsup.lavacampus.viewmodel.DetallePedidoViewModel
import com.tecsup.lavacampus.viewmodel.FiltroBandeja
import com.tecsup.lavacampus.viewmodel.GestionPedidoViewModel
import com.tecsup.lavacampus.viewmodel.InicioViewModel
import com.tecsup.lavacampus.viewmodel.LavaViewModelFactory
import com.tecsup.lavacampus.viewmodel.MisPedidosViewModel
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
import com.tecsup.lavacampus.viewmodel.PanelViewModel
import com.tecsup.lavacampus.viewmodel.ServiciosViewModel
import com.tecsup.lavacampus.viewmodel.SesionViewModel
import kotlinx.coroutines.flow.first

private data class ItemBarra(
    val titulo: String,
    val ruta: String,
    val icono: ImageVector
)

/** Navega a la raíz del perfil indicado y limpia TODA la pila (popUpTo). */
private fun irAPerfil(nav: NavHostController, rol: String) {
    val destino = if (rol == "ADMIN") Destino.RaizAdmin.ruta else Destino.RaizEstudiante.ruta
    nav.navigate(destino) {
        popUpTo(nav.graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

private fun irALogin(nav: NavHostController) {
    nav.navigate(Destino.Login.ruta) {
        popUpTo(nav.graph.id) { inclusive = true }
        launchSingleTop = true
    }
}

/** Cambio de pestaña de la barra inferior (guarda y restaura el estado de cada una). */
private fun irATab(nav: NavHostController, ruta: String, inicio: String) {
    nav.navigate(ruta) {
        popUpTo(inicio) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun NavLava(app: LavaApp) {
    val factory = remember { LavaViewModelFactory(app.repository, app.preferenciasStore) }
    val rootNav = rememberNavController()

    // Splash: si ya hay un perfil guardado se salta el login
    val destinoInicial by produceState<String?>(initialValue = null) {
        val rol = app.preferenciasStore.rol.first()
        value = when (rol) {
            "ADMIN" -> Destino.RaizAdmin.ruta
            "CLIENTE" -> Destino.RaizEstudiante.ruta
            else -> Destino.Login.ruta
        }
    }

    val inicio = destinoInicial
    if (inicio == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(navController = rootNav, startDestination = inicio) {
            composable(Destino.Login.ruta) {
                val authViewModel: AuthViewModel = viewModel(factory = factory)
                PantallaClara {
                    LoginScreen(
                        viewModel = authViewModel,
                        onLoginSuccess = { rol -> irAPerfil(rootNav, rol) }
                    )
                }
            }
            composable(Destino.RaizEstudiante.ruta) {
                EstudianteShell(
                    factory = factory,
                    onCambiarPerfil = { rol -> irAPerfil(rootNav, rol) },
                    onCerrarSesion = { irALogin(rootNav) }
                )
            }
            composable(Destino.RaizAdmin.ruta) {
                AdminShell(
                    factory = factory,
                    onCambiarPerfil = { rol -> irAPerfil(rootNav, rol) },
                    onCerrarSesion = { irALogin(rootNav) }
                )
            }
        }
    }
}

// =====================================================================
//  GRAFO DEL ESTUDIANTE: Inicio, Pedidos, Tarifas, Perfil
// =====================================================================
@Composable
private fun EstudianteShell(
    factory: LavaViewModelFactory,
    onCambiarPerfil: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    val items = listOf(
        ItemBarra("Inicio", Destino.EstudianteHome.ruta, Icons.Filled.Home),
        ItemBarra("Pedidos", Destino.MisPedidos.ruta, Icons.Filled.LocalLaundryService),
        ItemBarra("Tarifas", Destino.Tarifas.ruta, Icons.Filled.LocalOffer),
        ItemBarra("Perfil", Destino.Perfil.ruta, Icons.Filled.Person)
    )
    val rutasConBarra = items.map { it.ruta }
    val inicio = Destino.EstudianteHome.ruta

    Scaffold(
        bottomBar = {
            if (rutaActual in rutasConBarra) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = rutaActual == item.ruta,
                            onClick = { irATab(nav, item.ruta, inicio) },
                            icon = { Icon(item.icono, contentDescription = item.titulo) },
                            label = { Text(item.titulo) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = inicio,
            modifier = Modifier.padding(padding)
        ) {
            composable(Destino.EstudianteHome.ruta) {
                val vm: InicioViewModel = viewModel(factory = factory)
                PantallaClara {
                    InicioScreen(
                        inicioViewModel = vm,
                        onNuevoPedido = { nav.navigate(Destino.NuevoPedido.ruta) },
                        onVerPedidos = { irATab(nav, Destino.MisPedidos.ruta, inicio) },
                        onVerTarifas = { irATab(nav, Destino.Tarifas.ruta, inicio) },
                        onVerDetallePedido = { id ->
                            nav.navigate("${Destino.DetallePedido.ruta}/$id")
                        }
                    )
                }
            }

            composable(Destino.MisPedidos.ruta) {
                val vm: MisPedidosViewModel = viewModel(factory = factory)
                PantallaClara {
                    MisPedidosScreen(
                        viewModel = vm,
                        onPedidoClick = { id -> nav.navigate("${Destino.DetallePedido.ruta}/$id") },
                        onNuevoPedido = { nav.navigate(Destino.NuevoPedido.ruta) },
                        onRepetirPedido = { id -> nav.navigate(Destino.NuevoPedido.repetir(id)) }
                    )
                }
            }

            composable(Destino.Tarifas.ruta) {
                val vm: MisPedidosViewModel = viewModel(factory = factory)
                PantallaClara {
                    TarifasScreen(viewModel = vm)
                }
            }

            composable(Destino.Perfil.ruta) {
                val vm: SesionViewModel = viewModel(factory = factory)
                PerfilScreen(
                    viewModel = vm,
                    onCambiarPerfil = onCambiarPerfil,
                    onCerrarSesion = onCerrarSesion
                )
            }

            composable(
                route = "${Destino.NuevoPedido.ruta}?repetir={repetir}",
                arguments = listOf(
                    navArgument("repetir") {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { entry ->
                val repetirId = entry.arguments?.getInt("repetir") ?: -1
                val vm: NuevoPedidoViewModel = viewModel(factory = factory)
                LaunchedEffect(repetirId) {
                    if (repetirId != -1) {
                        vm.cargarDesdePedido(repetirId)
                    }
                }
                PantallaClara {
                    NuevoPedidoScreen(
                        nuevoPedidoViewModel = vm,
                        onVolver = { nav.popBackStack() },
                        onPedidoCreado = {
                            nav.navigate(Destino.MisPedidos.ruta) {
                                popUpTo(inicio)
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }

            composable(
                route = "${Destino.DetallePedido.ruta}/{pedidoId}",
                arguments = listOf(navArgument("pedidoId") { type = NavType.IntType })
            ) { entry ->
                val pedidoId = entry.arguments?.getInt("pedidoId") ?: 0
                val vm: DetallePedidoViewModel = viewModel(factory = factory)
                PantallaClara {
                    DetallePedidoScreen(
                        pedidoId = pedidoId,
                        detalleViewModel = vm,
                        onVolver = { nav.popBackStack() }
                    )
                }
            }
        }
    }
}

// =====================================================================
//  GRAFO DEL ADMINISTRADOR: Panel, Pedidos, Servicios, Clientes, Ajustes
// =====================================================================
@Composable
private fun AdminShell(
    factory: LavaViewModelFactory,
    onCambiarPerfil: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    // Compartidos entre pantallas del grafo (el Panel cambia el filtro de la Bandeja,
    // y el formulario lee la lista de servicios)
    val bandejaViewModel: BandejaViewModel = viewModel(factory = factory)
    val serviciosViewModel: ServiciosViewModel = viewModel(factory = factory)

    val items = listOf(
        ItemBarra("Panel", Destino.AdminHome.ruta, Icons.Filled.Dashboard),
        ItemBarra("Pedidos", Destino.Bandeja.ruta, Icons.Filled.Inbox),
        ItemBarra("Servicios", Destino.Servicios.ruta, Icons.Filled.LocalLaundryService),
        ItemBarra("Clientes", Destino.Clientes.ruta, Icons.Filled.People),
        ItemBarra("Ajustes", Destino.Ajustes.ruta, Icons.Filled.Settings)
    )
    val rutasConBarra = items.map { it.ruta }
    val inicio = Destino.AdminHome.ruta

    Scaffold(
        bottomBar = {
            if (rutaActual in rutasConBarra) {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = rutaActual == item.ruta,
                            onClick = { irATab(nav, item.ruta, inicio) },
                            icon = { Icon(item.icono, contentDescription = item.titulo) },
                            label = { Text(item.titulo) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = inicio,
            modifier = Modifier.padding(padding)
        ) {
            composable(Destino.AdminHome.ruta) {
                val vm: PanelViewModel = viewModel(factory = factory)
                PanelScreen(
                    viewModel = vm,
                    onVerListos = {
                        bandejaViewModel.cambiarFiltro(FiltroBandeja.LISTO)
                        irATab(nav, Destino.Bandeja.ruta, inicio)
                    }
                )
            }

            composable(Destino.Bandeja.ruta) {
                BandejaScreen(
                    viewModel = bandejaViewModel,
                    onAbrirPedido = { id -> nav.navigate("${Destino.GestionPedido.ruta}/$id") }
                )
            }

            composable(
                route = "${Destino.GestionPedido.ruta}/{pedidoId}",
                arguments = listOf(navArgument("pedidoId") { type = NavType.IntType })
            ) { entry ->
                val pedidoId = entry.arguments?.getInt("pedidoId") ?: 0
                val vm: GestionPedidoViewModel = viewModel(factory = factory)
                GestionPedidoScreen(
                    pedidoId = pedidoId,
                    viewModel = vm,
                    onVolver = { nav.popBackStack() }
                )
            }

            composable(Destino.Servicios.ruta) {
                ServiciosScreen(
                    viewModel = serviciosViewModel,
                    onNuevo = { nav.navigate("${Destino.FormularioServicio.ruta}/0") },
                    onEditar = { id -> nav.navigate("${Destino.FormularioServicio.ruta}/$id") }
                )
            }

            composable(
                route = "${Destino.FormularioServicio.ruta}/{servicioId}",
                arguments = listOf(navArgument("servicioId") { type = NavType.IntType })
            ) { entry ->
                val servicioId = entry.arguments?.getInt("servicioId") ?: 0
                FormularioServicioScreen(
                    servicioId = servicioId,
                    viewModel = serviciosViewModel,
                    onVolver = { nav.popBackStack() }
                )
            }

            composable(Destino.Clientes.ruta) {
                val vm: ClientesViewModel = viewModel(factory = factory)
                ClientesScreen(viewModel = vm)
            }

            composable(Destino.Ajustes.ruta) {
                val vm: SesionViewModel = viewModel(factory = factory)
                AjustesScreen(
                    viewModel = vm,
                    onCambiarPerfil = onCambiarPerfil,
                    onCerrarSesion = onCerrarSesion
                )
            }
        }
    }
}