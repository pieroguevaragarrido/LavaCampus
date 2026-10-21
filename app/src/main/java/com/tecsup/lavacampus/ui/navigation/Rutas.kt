package com.tecsup.lavacampus.ui.navigation

sealed class Ruta(val ruta: String) {
    object Splash : Ruta("splash")
    object Login : Ruta("login")

    // Estudiante
    object InicioEstudiante : Ruta("estudiante/inicio")
    object NuevoPedido : Ruta("estudiante/nuevo_pedido")
    object DetallePedido : Ruta("estudiante/detalle_pedido/{pedidoId}") {
        fun crearRuta(id: Int) = "estudiante/detalle_pedido/$id"
    }
    object MisPedidos : Ruta("estudiante/mis_pedidos")
    object Tarifas : Ruta("estudiante/tarifas")
    object Perfil : Ruta("estudiante/perfil")

    // Admin
    object PanelAdmin : Ruta("admin/panel")
    object BandejaAdmin : Ruta("admin/bandeja")
    object GestionPedido : Ruta("admin/gestion_pedido/{pedidoId}") {
        fun crearRuta(id: Int) = "admin/gestion_pedido/$id"
    }
    object ServiciosAdmin : Ruta("admin/servicios")
    object FormularioServicio : Ruta("admin/formulario_servicio?servicioId={servicioId}") {
        fun crearRuta(id: Int? = null) = if (id != null) "admin/formulario_servicio?servicioId=$id" else "admin/formulario_servicio"
    }
    object ClientesAdmin : Ruta("admin/clientes")
    object AjustesAdmin : Ruta("admin/ajustes")
}