package com.tecsup.lavacampus.data.local

import com.tecsup.lavacampus.data.local.entity.DetallePedido
import com.tecsup.lavacampus.data.local.entity.Pedido
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.model.Rol
import java.time.LocalDate

object DatosIniciales {
    suspend fun sembrar(db: LavaDatabase) {
        val usuarioDao = db.usuarioDao()
        if (usuarioDao.contar() > 0) return

        val servicioDao = db.servicioDao()
        val pedidoDao = db.pedidoDao()
        val detalleDao = db.detalleDao()

        // 1. Usuarios
        val usuarios = listOf(
            Usuario(id = 1, nombre = "Ana García", codigo = "U202310234", correo = "ana@correo.test", clave = "1234", rol = Rol.CLIENTE.name),
            Usuario(id = 2, nombre = "Luis Ramos", codigo = "U202010877", correo = "luis@correo.test", clave = "1234", rol = Rol.CLIENTE.name),
            Usuario(id = 3, nombre = "Marta Paredes", codigo = "U202411022", correo = "marta@correo.test", clave = "1234", rol = Rol.CLIENTE.name),
            Usuario(id = 4, nombre = "Administración", codigo = "", correo = "admin@lavacampus.test", clave = "admin", rol = Rol.ADMIN.name)
        )
        usuarioDao.insertarTodos(usuarios)

        // 2. Servicios
        val servicios = listOf(
            Servicio(id = 1, nombre = "Lavado y secado", descripcion = "Lavado por kilo ropa variada", unidad = "KG", precio = 8.0, horasEntrega = 24, activo = true, icono = "wash"),
            Servicio(id = 2, nombre = "Planchado", descripcion = "Planchado a vapor por prenda", unidad = "PRENDA", precio = 2.5, horasEntrega = 48, activo = true, icono = "iron"),
            Servicio(id = 3, nombre = "Lavado en seco", descripcion = "Limpieza al seco de ternos y vestidos", unidad = "PRENDA", precio = 12.0, horasEntrega = 48, activo = true, icono = "shirt"),
            Servicio(id = 4, nombre = "Edredón / frazada", descripcion = "Edredones, frazadas y cobertores", unidad = "UNIDAD", precio = 20.0, horasEntrega = 48, activo = false, icono = "wash")
        )
        servicioDao.insertarTodos(servicios)

        val hoy = LocalDate.now().toString()
        val ayer = LocalDate.now().minusDays(1).toString()
        val hace2dias = LocalDate.now().minusDays(2).toString()

        // 3. Pedidos para Ana (usuarioId = 1)
        val p12Id = pedidoDao.insertar(
            Pedido(usuarioId = 1, fechaRegistro = hoy, fechaEntrega = LocalDate.now().plusDays(2).toString(), estado = EstadoPedido.EN_PROCESO.name, total = 40.50, casillero = null, pagado = false, nota = "Ropa de color separada")
        )
        detalleDao.insertarTodos(
            listOf(
                DetallePedido(pedidoId = p12Id.toInt(), servicioId = 1, cantidad = 3.5, precioUnitario = 8.0, subtotal = 28.0),
                DetallePedido(pedidoId = p12Id.toInt(), servicioId = 2, cantidad = 5.0, precioUnitario = 2.5, subtotal = 12.5)
            )
        )

        val p11Id = pedidoDao.insertar(
            Pedido(usuarioId = 1, fechaRegistro = ayer, fechaEntrega = hoy, estado = EstadoPedido.LISTO.name, total = 16.00, casillero = "C-07", pagado = false, nota = "")
        )
        detalleDao.insertarTodos(
            listOf(
                DetallePedido(pedidoId = p11Id.toInt(), servicioId = 1, cantidad = 2.0, precioUnitario = 8.0, subtotal = 16.0)
            )
        )

        val p9Id = pedidoDao.insertar(
            Pedido(usuarioId = 1, fechaRegistro = hace2dias, fechaEntrega = ayer, estado = EstadoPedido.ENTREGADO.name, total = 36.00, casillero = "C-05", pagado = true, nota = "")
        )
        detalleDao.insertarTodos(
            listOf(
                DetallePedido(pedidoId = p9Id.toInt(), servicioId = 1, cantidad = 4.5, precioUnitario = 8.0, subtotal = 36.0)
            )
        )
    }
}