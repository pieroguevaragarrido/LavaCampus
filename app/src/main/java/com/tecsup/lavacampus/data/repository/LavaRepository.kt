package com.tecsup.lavacampus.data.repository

import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.ClienteResumen
import com.tecsup.lavacampus.model.IngresoServicio
import com.tecsup.lavacampus.model.LineaPedido
import kotlinx.coroutines.flow.Flow

/** Contrato del repositorio: los ViewModels SOLO conocen esta interface. */
interface LavaRepository {
    // Usuarios
    suspend fun login(correo: String, contrasena: String): Usuario?
    suspend fun obtenerUsuario(id: Int): Usuario?
    suspend fun primerUsuarioDeRol(rol: String): Usuario?
    fun observarClientes(): Flow<List<ClienteResumen>>

    // Servicios
    fun observarServiciosActivos(): Flow<List<Servicio>>
    fun observarServicios(): Flow<List<Servicio>>
    fun observarTodosServicios(): Flow<List<Servicio>>
    suspend fun cambiarEstadoServicio(servicioId: Int, activo: Boolean)
    suspend fun guardarServicio(servicio: Servicio)
    suspend fun eliminarServicio(servicio: Servicio)
    suspend fun servicioTieneDetalles(servicioId: Int): Boolean

    // Pedidos
    fun observarPedidosDeUsuario(usuarioId: Int): Flow<List<PedidoConDetalle>>
    fun observarPedidosPorUsuario(usuarioId: Int): Flow<List<PedidoConDetalle>>
    fun observarPedidoPorId(id: Int): Flow<PedidoConDetalle?>
    fun observarTodosPedidos(): Flow<List<PedidoConDetalle>>
    fun observarTodos(): Flow<List<PedidoConDetalle>>
    fun observarPedidos(): Flow<List<PedidoConDetalle>>
    fun observarCasillerosOcupados(): Flow<List<String>>
    suspend fun contarActivos(usuarioId: Int): Int
    suspend fun actualizarEstado(pedidoId: Int, nuevoEstado: String)
    suspend fun cambiarEstado(pedidoId: Int, nuevoEstado: Any, casillero: String? = null)
    suspend fun cambiarEstadoPedido(pedidoId: Int, nuevoEstado: String)
    suspend fun actualizarEstadoPedido(pedidoId: Int, nuevoEstado: String)
    suspend fun marcarPagado(pedidoId: Int, pagado: Boolean = true)
    fun observarIdsAtrasados(hoy: String): Flow<List<Int>>

    // Panel
    fun observarPedidosDeFecha(fecha: String): Flow<Int>
    fun observarCantidadPorEstado(estado: Any): Flow<Int>
    fun observarIngresosDesde(fecha: String): Flow<Double>
    fun observarIngresosPorServicio(fecha: String): Flow<List<IngresoServicio>>

    // Crear pedido
    suspend fun crearPedido(
        usuarioId: Int,
        lineas: List<LineaPedido>,
        fechaEntrega: String,
        nota: String,
        descuento: Double = 0.0
    ): Long
}