package com.tecsup.lavacampus.model

import androidx.compose.ui.graphics.Color

enum class Rol { CLIENTE, ADMIN }

enum class Unidad { KG, PRENDA, UNIDAD }

enum class EstadoPedido(val etiqueta: String, val color: Color) {
    RECIBIDO("Recibido", Color(0xFFD97706)),
    EN_PROCESO("En proceso", Color(0xFF2563EB)),
    LISTO("Listo", Color(0xFF16A34A)),
    ENTREGADO("Entregado", Color(0xFF475569)),
    CANCELADO("Cancelado", Color(0xFFDC2626));

    fun siguiente(): EstadoPedido? = when (this) {
        RECIBIDO -> EN_PROCESO
        EN_PROCESO -> LISTO
        LISTO -> ENTREGADO
        else -> null
    }
}

data class LineaPedido(
    val servicioId: Int,
    val nombreServicio: String,
    val cantidad: Double,
    val precioUnitario: Double
) {
    val subtotal: Double get() = cantidad * precioUnitario
}

data class IngresoServicio(
    val nombre: String,
    val total: Double
)

data class ClienteResumen(
    val id: Int,
    val nombre: String,
    val codigo: String,
    val totalPedidos: Int,
    val totalGastado: Double
)

object Casilleros {
    val todos: List<String> = (1..30).map { "C-%02d".format(it) }
}