package com.tecsup.lavacampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tecsup.lavacampus.data.local.entity.Pedido
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import kotlinx.coroutines.flow.Flow

@Dao
interface PedidoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(pedido: Pedido): Long

    @Transaction
    @Query("SELECT * FROM pedidos WHERE usuarioId = :usuarioId ORDER BY id DESC")
    fun observarPorUsuario(usuarioId: Int): Flow<List<PedidoConDetalle>>

    @Transaction
    @Query("SELECT * FROM pedidos WHERE id = :id LIMIT 1")
    fun observarPorId(id: Int): Flow<PedidoConDetalle?>

    @Query("SELECT COUNT(*) FROM pedidos WHERE usuarioId = :usuarioId AND estado IN ('RECIBIDO', 'EN_PROCESO', 'LISTO')")
    suspend fun contarActivos(usuarioId: Int): Int

    @Query("UPDATE pedidos SET estado = :estado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, estado: String)

    @Query("UPDATE pedidos SET estado = :estado, casillero = COALESCE(:casillero, casillero) WHERE id = :id")
    suspend fun cambiarEstadoYCasillero(id: Int, estado: String, casillero: String?)

    @Query("UPDATE pedidos SET pagado = :pagado WHERE id = :id")
    suspend fun marcarPagado(id: Int, pagado: Boolean)

    // Solo los casilleros de pedidos LISTO cuentan como ocupados
    @Query("SELECT DISTINCT casillero FROM pedidos WHERE casillero IS NOT NULL AND estado = 'LISTO'")
    fun observarCasillerosOcupados(): Flow<List<String>>

    @Transaction
    @Query("SELECT * FROM pedidos ORDER BY id DESC")
    fun observarTodos(): Flow<List<PedidoConDetalle>>

    // Pedidos de hoy SIN cancelados
    @Query("SELECT COUNT(*) FROM pedidos WHERE fechaRegistro = :fecha AND estado != 'CANCELADO'")
    fun observarPedidosDeFecha(fecha: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM pedidos WHERE estado = :estado")
    fun observarCantidadPorEstado(estado: String): Flow<Int>

    // Ingresos = suma del total de pedidos PAGADOS desde la fecha indicada
    @Query("SELECT COALESCE(SUM(total), 0.0) FROM pedidos WHERE pagado = 1 AND estado != 'CANCELADO' AND fechaRegistro >= :fecha")
    fun observarIngresosDesde(fecha: String): Flow<Double>
}