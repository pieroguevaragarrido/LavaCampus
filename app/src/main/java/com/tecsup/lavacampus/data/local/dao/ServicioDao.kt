package com.tecsup.lavacampus.data.local.dao

import androidx.room.*
import com.tecsup.lavacampus.data.local.entity.Servicio
import kotlinx.coroutines.flow.Flow

@Dao
interface ServicioDao {
    @Query("SELECT * FROM servicios ORDER BY nombre ASC")
    fun observarTodos(): Flow<List<Servicio>>

    @Query("SELECT * FROM servicios WHERE activo = 1 ORDER BY nombre ASC")
    fun observarActivos(): Flow<List<Servicio>>

    @Query("UPDATE servicios SET activo = :activo WHERE id = :id")
    suspend fun actualizarEstado(id: Int, activo: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(servicio: Servicio)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(servicios: List<Servicio>)
}