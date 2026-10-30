package com.tecsup.lavacampus.data.repository

import androidx.room.withTransaction
import com.tecsup.lavacampus.data.local.LavaDatabase
import com.tecsup.lavacampus.data.local.dao.DetalleDao
import com.tecsup.lavacampus.data.local.dao.PedidoDao
import com.tecsup.lavacampus.data.local.dao.ServicioDao
import com.tecsup.lavacampus.data.local.dao.UsuarioDao
import com.tecsup.lavacampus.data.local.entity.DetallePedido
import com.tecsup.lavacampus.data.local.entity.Pedido
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.ClienteResumen
import com.tecsup.lavacampus.model.IngresoServicio
import com.tecsup.lavacampus.model.LineaPedido
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class LavaRepositoryRoom(
    private val db: LavaDatabase,
    private val usuarioDao: UsuarioDao,
    private val servicioDao: ServicioDao,
    private val pedidoDao: PedidoDao,
    private val detalleDao: DetalleDao
) : LavaRepository {
    // ---------- Usuarios ----------
    override suspend fun login(correo: String, contrasena: String): Usuario? =
        usuarioDao.login(correo, contrasena)

    override suspend fun obtenerUsuario(id: Int): Usuario? = usuarioDao.obtenerPorId(id)

    override suspend fun primerUsuarioDeRol(rol: String): Usuario? = usuarioDao.primeroDeRol(rol)

    override fun observarClientes(): Flow<List<ClienteResumen>> = usuarioDao.observarClientes()

    // ---------- Servicios ----------
    override fun observarServiciosActivos(): Flow<List<Servicio>> = servicioDao.observarActivos()
    override fun observarServicios(): Flow<List<Servicio>> = servicioDao.observarTodos()
    override fun observarTodosServicios(): Flow<List<Servicio>> = servicioDao.observarTodos()

    override suspend fun cambiarEstadoServicio(servicioId: Int, activo: Boolean) {
        servicioDao.actualizarEstado(servicioId, activo)
    }

    /** id = 0 -> inserta; id > 0 -> actualiza (NO usar REPLACE: borraría la fila y rompería las FK). */
    override suspend fun guardarServicio(servicio: Servicio) {
        if (servicio.id == 0) servicioDao.guardar(servicio) else servicioDao.actualizar(servicio)
    }

    override suspend fun eliminarServicio(servicio: Servicio) {
        servicioDao.eliminar(servicio)
    }

    override suspend fun servicioTieneDetalles(servicioId: Int): Boolean =
        detalleDao.contarPorServicio(servicioId) > 0

    // ---------- Pedidos ----------
    override fun observarPedidosDeUsuario(usuarioId: Int): Flow<List<PedidoConDetalle>> =
        pedidoDao.observarPorUsuario(usuarioId)

    override fun observarPedidosPorUsuario(usuarioId: Int): Flow<List<PedidoConDetalle>> =
        pedidoDao.observarPorUsuario(usuarioId)

    override fun observarPedidoPorId(id: Int): Flow<PedidoConDetalle?> =
        pedidoDao.observarPorId(id)

    override fun observarTodosPedidos(): Flow<List<PedidoConDetalle>> =
        pedidoDao.observarTodos()

    override fun observarTodos(): Flow<List<PedidoConDetalle>> =
        pedidoDao.observarTodos()

    override fun observarPedidos(): Flow<List<PedidoConDetalle>> =
        pedidoDao.observarTodos()

    override fun observarCasillerosOcupados(): Flow<List<String>> =
        pedidoDao.observarCasillerosOcupados()

    override suspend fun contarActivos(usuarioId: Int): Int =
        pedidoDao.contarActivos(usuarioId)

    override suspend fun actualizarEstado(pedidoId: Int, nuevoEstado: String) {
        pedidoDao.actualizarEstado(pedidoId, nuevoEstado)
    }

    /** Guarda el estado (y el casillero si viene). Las reglas viven en el ViewModel. */
    override suspend fun cambiarEstado(pedidoId: Int, nuevoEstado: Any, casillero: String?) {
        val estadoStr = if (nuevoEstado is Enum<*>) nuevoEstado.name else nuevoEstado.toString()
        pedidoDao.cambiarEstadoYCasillero(pedidoId, estadoStr, casillero)
    }

    override suspend fun cambiarEstadoPedido(pedidoId: Int, nuevoEstado: String) {
        pedidoDao.actualizarEstado(pedidoId, nuevoEstado)
    }

    override suspend fun actualizarEstadoPedido(pedidoId: Int, nuevoEstado: String) {
        pedidoDao.actualizarEstado(pedidoId, nuevoEstado)
    }

    override suspend fun marcarPagado(pedidoId: Int, pagado: Boolean) {
        pedidoDao.marcarPagado(pedidoId, pagado)
    }

    // ---------- Panel ----------
    override fun observarPedidosDeFecha(fecha: String): Flow<Int> =
        pedidoDao.observarPedidosDeFecha(fecha)

    override fun observarCantidadPorEstado(estado: Any): Flow<Int> {
        val estadoStr = if (estado is Enum<*>) estado.name else estado.toString()
        return pedidoDao.observarCantidadPorEstado(estadoStr)
    }

    override fun observarIngresosDesde(fecha: String): Flow<Double> =
        pedidoDao.observarIngresosDesde(fecha)

    override fun observarIngresosPorServicio(fecha: String): Flow<List<IngresoServicio>> =
        detalleDao.observarIngresosPorServicio(fecha)

    // ---------- Crear pedido (UNA transacción) ----------
    override suspend fun crearPedido(
        usuarioId: Int,
        lineas: List<LineaPedido>,
        fechaEntrega: String,
        nota: String
    ): Long = db.withTransaction {
        val total = lineas.sumOf { it.subtotal }
        val pedido = Pedido(
            usuarioId = usuarioId,
            fechaRegistro = LocalDate.now().toString(),
            fechaEntrega = fechaEntrega,
            estado = "RECIBIDO",
            total = total,
            casillero = null,
            pagado = false,
            nota = nota
        )
        val pedidoId = pedidoDao.insertar(pedido).toInt()

        val detalles = lineas.map { linea ->
            DetallePedido(
                pedidoId = pedidoId,
                servicioId = linea.servicioId,
                cantidad = linea.cantidad,
                precioUnitario = linea.precioUnitario,
                subtotal = linea.subtotal
            )
        }
        detalleDao.insertarTodos(detalles)
        pedidoId.toLong()
    }
}