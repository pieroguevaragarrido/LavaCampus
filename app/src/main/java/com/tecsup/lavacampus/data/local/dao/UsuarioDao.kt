package com.tecsup.lavacampus.data.local.dao

import androidx.room.*
import com.tecsup.lavacampus.data.local.entity.Usuario

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
}