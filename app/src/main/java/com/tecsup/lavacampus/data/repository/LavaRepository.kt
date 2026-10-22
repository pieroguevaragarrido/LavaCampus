package com.tecsup.lavacampus.data.repository

import com.tecsup.lavacampus.data.local.dao.*
import com.tecsup.lavacampus.data.local.entity.*
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class LavaRepository(
    private val usuarioDao: UsuarioDao,
    private val servicioDao: ServicioDao,
    private val pedidoDao: PedidoDao,
    private val detalleDao: DetalleDao
) {
    // Auth
    suspend fun login(correo: String, contrasena: String): Usuario? =
        usuarioDao.login(correo, contrasena)

    fun observarClientes(): Flow<List<ClienteResumen>> = flowOf(emptyList())

    // Servicios
    fun observarServiciosActivos(): Flow<List<Servicio>> = servicioDao.observarActivos()
    fun observarServicios(): Flow<List<Servicio>> = servicioDao.observarTodos()
    fun observarTodosServicios(): Flow<List<Servicio>> = servicioDao.observarTodos()

    suspend fun cambiarEstadoServicio(servicioId: Int, activo: Boolean) {
        servicioDao.actualizarEstado(servicioId, activo)
    }

    suspend fun guardarServicio(servicio: Servicio) {
        servicioDao.guardar(servicio)
    }

    // Pedidos
    fun observarPedidosDeUsuario(usuarioId: Int): Flow<List<PedidoConDetalle>> =
        pedidoDao.observarPorUsuario(usuarioId)

    fun observarPedidosPorUsuario(usuarioId: Int): Flow<List<PedidoConDetalle>> =
        pedidoDao.observarPorUsuario(usuarioId)

    fun observarPedidoPorId(id: Int): Flow<PedidoConDetalle?> =
        pedidoDao.observarPorId(id)

    fun observarTodosPedidos(): Flow<List<PedidoConDetalle>> =
        pedidoDao.observarTodos()

    fun observarTodos(): Flow<List<PedidoConDetalle>> =
        pedidoDao.observarTodos()

    fun observarPedidos(): Flow<List<PedidoConDetalle>> =
        pedidoDao.observarTodos()

    fun observarCasillerosOcupados(): Flow<List<String>> =
        pedidoDao.observarCasillerosOcupados()

    suspend fun contarActivos(usuarioId: Int): Int =
        pedidoDao.contarActivos(usuarioId)

    suspend fun actualizarEstado(pedidoId: Int, nuevoEstado: String) {
        pedidoDao.actualizarEstado(pedidoId, nuevoEstado)
    }

    suspend fun cambiarEstado(pedidoId: Int, nuevoEstado: Any, casillero: String? = null) {
        val estadoStr = if (nuevoEstado is Enum<*>) nuevoEstado.name else nuevoEstado.toString()
        pedidoDao.cambiarEstadoYCasillero(pedidoId, estadoStr, casillero)
    }

    suspend fun cambiarEstadoPedido(pedidoId: Int, nuevoEstado: String) {
        pedidoDao.actualizarEstado(pedidoId, nuevoEstado)
    }

    suspend fun actualizarEstadoPedido(pedidoId: Int, nuevoEstado: String) {
        pedidoDao.actualizarEstado(pedidoId, nuevoEstado)
    }

    suspend fun marcarPagado(pedidoId: Int, pagado: Boolean = true) {
        pedidoDao.marcarPagado(pedidoId, pagado)
    }

    // Panel / Dashboard Admin
    fun observarPedidosDeFecha(fecha: String): Flow<Int> =
        pedidoDao.observarPedidosDeFecha(fecha)

    fun observarCantidadPorEstado(estado: Any): Flow<Int> {
        val estadoStr = if (estado is Enum<*>) estado.name else estado.toString()
        return pedidoDao.observarCantidadPorEstado(estadoStr)
    }

    fun observarIngresosDesde(fecha: String): Flow<Double> =
        pedidoDao.observarIngresosDesde(fecha)

    fun observarIngresosPorServicio(fecha: String): Flow<List<IngresoServicio>> =
        flowOf(emptyList())

    // Crear Pedido
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