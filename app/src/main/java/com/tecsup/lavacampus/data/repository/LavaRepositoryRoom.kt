package com.tecsup.lavacampus.data.repository

import com.tecsup.lavacampus.data.local.LavaDatabase
import com.tecsup.lavacampus.data.local.entity.DetallePedido
import com.tecsup.lavacampus.data.local.entity.Pedido
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class LavaRepositoryRoom(private val db: LavaDatabase) : LavaRepository {

    private val usuarioDao = db.usuarioDao()
    private val servicioDao = db.servicioDao()
    private val pedidoDao = db.pedidoDao()
    private val detalleDao = db.detalleDao()

    // --- Usuario ---
    override suspend fun login(correo: String, clave: String): Usuario? {
        return usuarioDao.login(correo, clave)
    }

    override fun observarClientes(): Flow<List<ClienteResumen>> {
        return usuarioDao.observarClientes()
    }

    // --- Servicios ---
    override fun observarServicios(): Flow<List<Servicio>> {
        return servicioDao.observarTodos()
    }

    override fun observarServiciosActivos(): Flow<List<Servicio>> {
        return servicioDao.observarActivos()
    }

    override suspend fun cambiarEstadoServicio(id: Int, activo: Boolean) {
        servicioDao.actualizarEstado(id, activo)
    }

    override suspend fun guardarServicio(s: Servicio) {
        servicioDao.insertar(s)
    }

    // --- Pedidos ---
    override suspend fun crearPedido(
        usuarioId: Int,
        lineas: List<LineaPedido>,
        fechaEntrega: String,
        nota: String
    ): Long {
        val totalCalculado = lineas.sumOf { it.cantidad * it.precioUnitario }
        val nuevoPedido = Pedido(
            usuarioId = usuarioId,
            fechaRegistro = LocalDate.now().toString(),
            fechaEntrega = fechaEntrega,
            estado = EstadoPedido.RECIBIDO.name,
            total = totalCalculado,
            casillero = null,
            pagado = false,
            nota = nota
        )
        val pedidoId = pedidoDao.insertar(nuevoPedido)
        val detalles = lineas.map { linea ->
            DetallePedido(
                pedidoId = pedidoId.toInt(),
                servicioId = linea.servicioId,
                cantidad = linea.cantidad,
                precioUnitario = linea.precioUnitario,
                subtotal = linea.cantidad * linea.precioUnitario
            )
        }
        detalleDao.insertarTodos(detalles)
        return pedidoId
    }

    override suspend fun contarActivos(usuarioId: Int): Int {
        return pedidoDao.contarActivos(usuarioId)
    }

    override fun observarPedidosDeUsuario(usuarioId: Int): Flow<List<PedidoConDetalle>> {
        return pedidoDao.observarPorUsuario(usuarioId)
    }

    override fun observarPedidos(): Flow<List<PedidoConDetalle>> {
        return pedidoDao.observarTodos()
    }

    override fun observarPedido(id: Int): Flow<PedidoConDetalle?> {
        return pedidoDao.observarUno(id)
    }

    override suspend fun cambiarEstado(id: Int, estado: EstadoPedido, casillero: String?) {
        pedidoDao.actualizarEstado(id, estado.name, casillero)
    }

    override suspend fun marcarPagado(id: Int, pagado: Boolean) {
        pedidoDao.actualizarPagado(id, pagado)
    }

    override fun observarCasillerosOcupados(): Flow<List<String>> {
        return pedidoDao.observarCasillerosOcupados()
    }

    // --- Panel ---
    override fun observarPedidosDeFecha(fecha: String): Flow<Int> {
        return pedidoDao.contarPorFecha(fecha)
    }

    override fun observarCantidadPorEstado(estado: EstadoPedido): Flow<Int> {
        return pedidoDao.contarPorEstado(estado.name)
    }

    override fun observarIngresosDesde(fechaInicio: String): Flow<Double> {
        return pedidoDao.ingresosDesde(fechaInicio)
    }

    override fun observarIngresosPorServicio(fechaInicio: String): Flow<List<IngresoServicio>> {
        return detalleDao.observarIngresosPorServicio(fechaInicio)
    }
}