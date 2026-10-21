package com.tecsup.lavacampus.data.local

import com.tecsup.lavacampus.data.local.entity.*
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.model.Rol
import java.time.LocalDate

object DatosIniciales {

    suspend fun sembrar(db: LavaDatabase) {
        if (db.usuarioDao().contar() > 0) return

        val hoy = LocalDate.now().toString()
        val hace1Dia = LocalDate.now().minusDays(1).toString()
        val hace2Dias = LocalDate.now().minusDays(2).toString()
        val hace3Dias = LocalDate.now().minusDays(3).toString()

        // 1. Servicios iniciales
        val servicios = listOf(
            Servicio(1, "Lavado y secado", "Lavado completo por kilo con detergente premium", "KG", 8.0, 24, true, "wash"),
            Servicio(2, "Planchado", "Planchado a vapor profesional por prenda", "PRENDA", 2.5, 48, true, "iron"),
            Servicio(3, "Lavado en seco", "Tratamiento especial para ternos, sacos y vestidos", "PRENDA", 12.0, 48, true, "shirt"),
            Servicio(4, "Edredón / frazada", "Lavado profundo por pieza (desactivado temporalmente)", "UNIDAD", 20.0, 48, false, "wash")
        )
        db.servicioDao().insertarTodos(servicios)

        // 2. Usuarios
        val usuarios = listOf(
            Usuario(1, "Ana García", "U202310234", "ana@correo.test", "1234", Rol.CLIENTE.name),
            Usuario(2, "Luis Ramos", "U202310877", "luis@correo.test", "1234", Rol.CLIENTE.name),
            Usuario(3, "Marta Paredes", "U202311022", "marta@correo.test", "1234", Rol.CLIENTE.name),
            Usuario(4, "Administrador", "", "admin@lavacampus.test", "admin", Rol.ADMIN.name)
        )
        db.usuarioDao().insertarTodos(usuarios)

        // 3. Pedidos y sus detalles
        val p1 = Pedido(0, 1, hace3Dias, hace1Dia, EstadoPedido.ENTREGADO.name, 36.0, "C-01", true, "Ropa delicada")
        val d1 = listOf(DetallePedido(0, 0, 1, 2.0, 8.0, 16.0), DetallePedido(0, 0, 3, 1.0, 12.0, 12.0))

        val p2 = Pedido(0, 1, hace2Dias, hoy, EstadoPedido.ENTREGADO.name, 28.0, "C-02", true, "")
        val d2 = listOf(DetallePedido(0, 0, 1, 3.5, 8.0, 28.0))

        val p3 = Pedido(0, 2, hace2Dias, hoy, EstadoPedido.ENTREGADO.name, 22.5, "C-03", true, "")
        val d3 = listOf(DetallePedido(0, 0, 1, 2.0, 8.0, 16.0), DetallePedido(0, 0, 2, 2.0, 2.5, 5.0))

        val p4 = Pedido(0, 3, hace1Dia, hoy, EstadoPedido.LISTO.name, 16.0, "C-05", false, "Recoger tarde")
        val d4 = listOf(DetallePedido(0, 0, 1, 2.0, 8.0, 16.0))

        val p5 = Pedido(0, 1, hace1Dia, hoy, EstadoPedido.LISTO.name, 24.0, "C-07", false, "")
        val d5 = listOf(DetallePedido(0, 0, 3, 2.0, 12.0, 24.0))

        val p6 = Pedido(0, 2, hace1Dia, hoy, EstadoPedido.EN_PROCESO.name, 40.0, null, false, "")
        val d6 = listOf(DetallePedido(0, 0, 1, 5.0, 8.0, 40.0))

        val p7 = Pedido(0, 3, hoy, LocalDate.now().plusDays(2).toString(), EstadoPedido.EN_PROCESO.name, 18.0, null, false, "")
        val d7 = listOf(DetallePedido(0, 0, 1, 1.0, 8.0, 8.0), DetallePedido(0, 0, 2, 4.0, 2.5, 10.0))

        val p8 = Pedido(0, 1, hoy, LocalDate.now().plusDays(1).toString(), EstadoPedido.RECIBIDO.name, 40.5, null, false, "Ropa de color separada")
        val d8 = listOf(DetallePedido(0, 0, 1, 3.5, 8.0, 28.0), DetallePedido(0, 0, 2, 5.0, 2.5, 12.5))

        val p9 = Pedido(0, 2, hoy, LocalDate.now().plusDays(1).toString(), EstadoPedido.RECIBIDO.name, 16.0, null, false, "")
        val d9 = listOf(DetallePedido(0, 0, 1, 2.0, 8.0, 16.0))

        val p10 = Pedido(0, 3, hace3Dias, hace1Dia, EstadoPedido.CANCELADO.name, 12.0, null, false, "Cancelado por usuario")
        val d10 = listOf(DetallePedido(0, 0, 2, 4.8, 2.5, 12.0))

        val listaSemilla = listOf(
            Pair(p1, d1), Pair(p2, d2), Pair(p3, d3), Pair(p4, d4), Pair(p5, d5),
            Pair(p6, d6), Pair(p7, d7), Pair(p8, d8), Pair(p9, d9), Pair(p10, d10)
        )

        for ((pedido, detalles) in listaSemilla) {
            val pedidoId = db.pedidoDao().insertar(pedido).toInt()
            val detallesAjustados = detalles.map { it.copy(pedidoId = pedidoId) }
            db.detalleDao().insertarTodos(detallesAjustados)
        }
    }
}