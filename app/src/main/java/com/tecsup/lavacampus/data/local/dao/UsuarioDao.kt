package com.tecsup.lavacampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.model.ClienteResumen
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contarUsuarios(): Int

    @Query("SELECT * FROM usuarios WHERE correo = :correo AND (contrasena = :contrasena OR clave = :contrasena) LIMIT 1")
    suspend fun login(correo: String, contrasena: String): Usuario?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(usuarios: List<Usuario>)

    @Query("SELECT * FROM usuarios WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): Usuario?

    @Query("SELECT * FROM usuarios WHERE rol = :rol ORDER BY id LIMIT 1")
    suspend fun primeroDeRol(rol: String): Usuario?

    @Query(
        """
        SELECT u.id AS id,
               u.nombre AS nombre,
               u.codigo AS codigo,
               COUNT(CASE WHEN p.estado != 'CANCELADO' THEN 1 END) AS totalPedidos,
               COALESCE(SUM(CASE WHEN p.pagado = 1 AND p.estado != 'CANCELADO' THEN p.total ELSE 0.0 END), 0.0) AS totalGastado
        FROM usuarios u
        LEFT JOIN pedidos p ON p.usuarioId = u.id
        WHERE u.rol = 'CLIENTE'
        GROUP BY u.id
        ORDER BY u.nombre
        """
    )
    fun observarClientes(): Flow<List<ClienteResumen>>
}