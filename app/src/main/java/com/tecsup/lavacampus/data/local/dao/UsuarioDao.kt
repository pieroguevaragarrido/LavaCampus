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
    suspend fun insertar(usuario: Usuario): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(usuarios: List<Usuario>)

    @Query("""
        SELECT u.id AS id, 
               u.nombre AS nombre, 
               u.codigo AS codigo, 
               u.correo AS correo,
               COUNT(p.id) AS totalPedidos,
               COALESCE(SUM(CASE WHEN p.pagado = 1 THEN p.total ELSE 0.0 END), 0.0) AS totalGastado
        FROM usuarios u
        LEFT JOIN pedidos p ON u.id = p.usuarioId AND p.estado != 'CANCELADO'
        WHERE u.rol = 'CLIENTE'
        GROUP BY u.id
    """)
    fun observarClientes(): Flow<List<ClienteResumen>>
}