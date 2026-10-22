package com.tecsup.lavacampus.data.repository

import com.tecsup.lavacampus.data.local.dao.*
import com.tecsup.lavacampus.data.local.entity.*
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.LineaPedido
import kotlinx.coroutines.flow.Flow

class LavaRepositoryRoom(
    private val usuarioDao: UsuarioDao,
    private val servicioDao: ServicioDao,
    private val pedidoDao: PedidoDao,
    private val detalleDao: DetalleDao
) {
    suspend fun login(correo: String, contrasena: String): Usuario? =
        usuarioDao.login(correo, contrasena)

    fun observarServiciosActivos(): Flow<List<Servicio>> = servicioDao.observarActivos()

    fun observarPedidosDeUsuario(usuarioId: Int): Flow<List<PedidoConDetalle>> =
        pedidoDao.observarPorUsuario(usuarioId)

    fun observarPedidoPorId(id: Int): Flow<PedidoConDetalle?> =
        pedidoDao.observarPorId(id)

    suspend fun contarActivos(usuarioId: Int): Int =
        pedidoDao.contarActivos(usuarioId)

    suspend fun actualizarEstado(pedidoId: Int, nuevoEstado: String) {
        pedidoDao.actualizarEstado(pedidoId, nuevoEstado)
    }

    suspend fun crearPedido(
        usuarioId: Int,
        lineas: List<LineaPedido>,
        fechaEntrega: String,
        nota: String
    ): Long {
        val total = lineas.sumOf { it.subtotal }
        val pedido = Pedido(
            usuarioId = usuarioId,
            fechaRegistro = java.time.LocalDate.now().toString(),
            fechaEntrega = fechaEntrega,
            estado = "RECIBIDO",
            total = total,
            casillero = null,
            pagado = false,
            nota = nota
        )
        val pedidoId = pedidoDao.insertar(pedido).toInt()

        val detalles = lineas.map { line ->
            DetallePedido(
                pedidoId = pedidoId,
                servicioId = line.servicioId,
                cantidad = line.cantidad,
                precioUnitario = line.precioUnitario,
                subtotal = line.subtotal
            )
        }
        detalleDao.insertarTodos(detalles)
        return pedidoId.toLong()
    }
}