package com.tecsup.lavacampus.data.repository

import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.*
import kotlinx.coroutines.flow.Flow

interface LavaRepository {
    suspend fun login(correo: String, clave: String): Usuario?
    fun observarClientes(): Flow<List<ClienteResumen>>

    fun observarServicios(): Flow<List<Servicio>>
    fun observarServiciosActivos(): Flow<List<Servicio>>
    suspend fun cambiarEstadoServicio(id: Int, activo: Boolean)
    suspend fun guardarServicio(s: Servicio)

    suspend fun crearPedido(
        usuarioId: Int,
        lineas: List<LineaPedido>,
        fechaEntrega: String,
        nota: String
    ): Long

    suspend fun contarActivos(usuarioId: Int): Int
    fun observarPedidosDeUsuario(usuarioId: Int): Flow<List<PedidoConDetalle>>
    fun observarPedidos(): Flow<List<PedidoConDetalle>>
    fun observarPedido(id: Int): Flow<PedidoConDetalle?>
    suspend fun cambiarEstado(id: Int, estado: EstadoPedido, casillero: String?)
    suspend fun marcarPagado(id: Int, pagado: Boolean)
    fun observarCasillerosOcupados(): Flow<List<String>>

    fun observarPedidosDeFecha(fecha: String): Flow<Int>
    fun observarCantidadPorEstado(estado: EstadoPedido): Flow<Int>
    fun observarIngresosDesde(fechaInicio: String): Flow<Double>
    fun observarIngresosPorServicio(fechaInicio: String): Flow<List<IngresoServicio>>
}