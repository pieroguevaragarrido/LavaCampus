package com.tecsup.lavacampus.data.local.dao

import androidx.room.*
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.model.ClienteResumen
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND clave = :clave LIMIT 1")
    suspend fun login(correo: String, clave: String): Usuario?

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(lista: List<Usuario>)

    @Query("""
        SELECT 
            u.id AS id,
            u.nombre AS nombre,
            u.codigo AS codigo,
            COUNT(CASE WHEN p.estado != 'CANCELADO' THEN p.id END) AS totalPedidos,
            COALESCE(SUM(CASE WHEN p.pagado = 1 THEN p.total ELSE 0.0 END), 0.0) AS totalGastado
        FROM usuarios u
        LEFT JOIN pedidos p ON u.id = p.usuarioId
        WHERE u.rol = 'CLIENTE'
        GROUP BY u.id
        ORDER BY u.nombre
    """)
    fun observarClientes(): Flow<List<ClienteResumen>>
}