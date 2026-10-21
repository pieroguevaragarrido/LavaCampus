package com.tecsup.lavacampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tecsup.lavacampus.data.local.dao.DetalleDao
import com.tecsup.lavacampus.data.local.dao.PedidoDao
import com.tecsup.lavacampus.data.local.dao.ServicioDao
import com.tecsup.lavacampus.data.local.dao.UsuarioDao
import com.tecsup.lavacampus.data.local.entity.DetallePedido
import com.tecsup.lavacampus.data.local.entity.Pedido
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario

@Database(
    entities = [Usuario::class, Servicio::class, Pedido::class, DetallePedido::class],
    version = 1,
    exportSchema = false
)
abstract class LavaDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun servicioDao(): ServicioDao
    abstract fun pedidoDao(): PedidoDao
    abstract fun detalleDao(): DetalleDao

    companion object {
        @Volatile
        private var INSTANCE: LavaDatabase? = null

        fun obtener(context: Context): LavaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LavaDatabase::class.java,
                    "lavacampus.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}