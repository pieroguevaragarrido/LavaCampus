package com.tecsup.lavacampus.ui.navigation

sealed class Destino(val ruta: String) {
    // Raíz de la app
    object Login : Destino("login")
    object RaizEstudiante : Destino("raiz_estudiante")
    object RaizAdmin : Destino("raiz_admin")

    // Grafo del estudiante
    object EstudianteHome : Destino("estudiante_home")
    object NuevoPedido : Destino("nuevo_pedido") {
        fun repetir(pedidoId: Int) = "nuevo_pedido?repetir=$pedidoId"
    }
    object MisPedidos : Destino("mis_pedidos")
    object DetallePedido : Destino("detalle_pedido")
    object Tarifas : Destino("tarifas")
    object Perfil : Destino("perfil")

    // Grafo del administrador
    object AdminHome : Destino("admin_home")
    object Bandeja : Destino("bandeja")
    object GestionPedido : Destino("gestion_pedido")
    object Servicios : Destino("servicios")
    object FormularioServicio : Destino("formulario_servicio")
    object Clientes : Destino("clientes")
    object Ajustes : Destino("ajustes")
}

typealias Rutas = Destino