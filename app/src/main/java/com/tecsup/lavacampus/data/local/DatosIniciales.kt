package com.tecsup.lavacampus.data.local

import androidx.room.withTransaction
import com.tecsup.lavacampus.data.local.entity.DetallePedido
import com.tecsup.lavacampus.data.local.entity.Pedido
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.model.Rol
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

object DatosIniciales {

    // Una línea de detalle: servicio, cantidad y precio unitario
    private data class Linea(val servicioId: Int, val cantidad: Double, val precio: Double)

    suspend fun sembrar(db: LavaDatabase) {
        // Todo en una transacción y con la comprobación dentro: así no se duplican los datos
        // aunque sembrar() se llame dos veces a la vez.
        db.withTransaction {
            if (db.usuarioDao().contar() > 0) return@withTransaction

            val usuarioDao = db.usuarioDao()
            val servicioDao = db.servicioDao()
            val pedidoDao = db.pedidoDao()
            val detalleDao = db.detalleDao()

            // 1. Usuarios (datos ficticios)
            usuarioDao.insertarTodos(
                listOf(
                    Usuario(id = 1, nombre = "Ana García", codigo = "U202310234", correo = "ana@correo.test", clave = "1234", rol = Rol.CLIENTE.name),
                    Usuario(id = 2, nombre = "Luis Ramos", codigo = "U202010877", correo = "luis@correo.test", clave = "1234", rol = Rol.CLIENTE.name),
                    Usuario(id = 3, nombre = "Marta Paredes", codigo = "U202411022", correo = "marta@correo.test", clave = "1234", rol = Rol.CLIENTE.name),
                    Usuario(id = 4, nombre = "Administración", codigo = "", correo = "admin@lavacampus.test", clave = "admin", rol = Rol.ADMIN.name)
                )
            )

            // 2. Servicios
            servicioDao.insertarTodos(
                listOf(
                    Servicio(id = 1, nombre = "Lavado y secado", descripcion = "Lavado por kilo de ropa variada", unidad = "KG", precio = 8.0, horasEntrega = 24, activo = true, icono = "wash"),
                    Servicio(id = 2, nombre = "Planchado", descripcion = "Planchado a vapor por prenda", unidad = "PRENDA", precio = 2.5, horasEntrega = 48, activo = true, icono = "iron"),
                    Servicio(id = 3, nombre = "Lavado en seco", descripcion = "Limpieza en seco de ternos y vestidos", unidad = "PRENDA", precio = 12.0, horasEntrega = 48, activo = true, icono = "shirt"),
                    Servicio(id = 4, nombre = "Edredón / frazada", descripcion = "Edredones, frazadas y cobertores", unidad = "UNIDAD", precio = 20.0, horasEntrega = 48, activo = false, icono = "wash")
                )
            )

            // 3. Fechas relativas: siempre hay pedidos de hoy y de ESTA semana
            val hoyFecha = LocalDate.now()
            val hoy = hoyFecha.toString()
            val ayer = hoyFecha.minusDays(1).toString()
            val lunes = hoyFecha.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toString()
            val manana = hoyFecha.plusDays(1).toString()
            val pasado = hoyFecha.plusDays(2).toString()

            // Inserta un pedido y sus detalles; el total sale de la suma de las líneas
            suspend fun pedido(
                usuarioId: Int,
                registro: String,
                entrega: String,
                estado: EstadoPedido,
                casillero: String?,
                pagado: Boolean,
                nota: String,
                lineas: List<Linea>
            ) {
                val total = lineas.sumOf { it.cantidad * it.precio }
                val id = pedidoDao.insertar(
                    Pedido(
                        usuarioId = usuarioId,
                        fechaRegistro = registro,
                        fechaEntrega = entrega,
                        estado = estado.name,
                        total = total,
                        casillero = casillero,
                        pagado = pagado,
                        nota = nota
                    )
                ).toInt()
                detalleDao.insertarTodos(
                    lineas.map {
                        DetallePedido(
                            pedidoId = id,
                            servicioId = it.servicioId,
                            cantidad = it.cantidad,
                            precioUnitario = it.precio,
                            subtotal = it.cantidad * it.precio
                        )
                    }
                )
            }

            // Ana (id 1)
            pedido(1, hoy, pasado, EstadoPedido.EN_PROCESO, null, false, "Ropa de color separada",
                listOf(Linea(1, 3.5, 8.0), Linea(2, 5.0, 2.5)))
            pedido(1, ayer, hoy, EstadoPedido.LISTO, "C-07", false, "",
                listOf(Linea(1, 2.0, 8.0)))
            pedido(1, lunes, lunes, EstadoPedido.ENTREGADO, "C-03", true, "",
                listOf(Linea(1, 4.5, 8.0)))

            // Luis (id 2)
            pedido(2, hoy, pasado, EstadoPedido.RECIBIDO, null, false, "",
                listOf(Linea(2, 4.0, 2.5), Linea(3, 1.0, 12.0)))
            pedido(2, ayer, hoy, EstadoPedido.LISTO, "C-05", false, "",
                listOf(Linea(1, 3.0, 8.0)))
            pedido(2, lunes, lunes, EstadoPedido.ENTREGADO, "C-10", true, "",
                listOf(Linea(3, 2.0, 12.0), Linea(2, 3.0, 2.5)))

            // Marta (id 3)
            pedido(3, ayer, manana, EstadoPedido.EN_PROCESO, null, false, "Con suavizante",
                listOf(Linea(1, 5.0, 8.0)))
            pedido(3, hoy, pasado, EstadoPedido.RECIBIDO, null, false, "",
                listOf(Linea(1, 2.5, 8.0), Linea(2, 6.0, 2.5)))
            pedido(3, lunes, lunes, EstadoPedido.ENTREGADO, "C-12", true, "",
                listOf(Linea(1, 6.0, 8.0)))
            pedido(3, ayer, manana, EstadoPedido.CANCELADO, null, false, "",
                listOf(Linea(2, 4.0, 2.5)))
        }
    }
}