package com.tecsup.lavacampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.tecsup.lavacampus.data.local.dao.*
import com.tecsup.lavacampus.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        fun obtenerBaseDatos(context: Context): LavaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LavaDatabase::class.java,
                    "lava_campus.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    DatosIniciales.sembrar(database)
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance

                // Verificación de seguridad: Si la tabla está vacía, puebla los datos iniciales
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        if (instance.usuarioDao().contar() == 0) {
                            DatosIniciales.sembrar(instance)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                instance
            }
        }
    }
}