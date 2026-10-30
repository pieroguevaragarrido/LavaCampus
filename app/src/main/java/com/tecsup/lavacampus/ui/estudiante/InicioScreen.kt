package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.viewmodel.InicioViewModel

@Composable
fun InicioScreen(
    inicioViewModel: InicioViewModel,
    onNuevoPedido: () -> Unit,
    onVerPedidos: () -> Unit,
    onVerTarifas: () -> Unit,
    onVerDetallePedido: (Int) -> Unit
) {
    val nombre by inicioViewModel.nombre.collectAsState()
    val codigo by inicioViewModel.codigo.collectAsState()
    val pedidoActivo by inicioViewModel.pedidoActivo.collectAsState()
    val recientes by inicioViewModel.recientes.collectAsState()

    val colorTurquesa = Color(0xFF0891B2)
    val colorFondo = Color(0xFFF0FCFF)

    Scaffold(
        containerColor = colorFondo
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Saludo e Icono
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hola, ${nombre ?: "Ana"}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "${codigo ?: ""} · Tecsup",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificaciones",
                            tint = colorTurquesa
                        )
                    }
                }
            }

            // Tarjeta de Pedido Activo
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = colorTurquesa),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            pedidoActivo?.let { onVerDetallePedido(it.pedido.id) }
                        }
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tu pedido activo",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp
                            )
                            if (pedidoActivo != null) {
                                Text(
                                    text = "#${pedidoActivo!!.pedido.id.toString().padStart(3, '0')}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        if (pedidoActivo != null) {
                            val pedido = pedidoActivo!!.pedido
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (pedido.estado) {
                                    EstadoPedido.RECIBIDO.name -> "Recibido"
                                    EstadoPedido.EN_PROCESO.name -> "En proceso"
                                    EstadoPedido.LISTO.name -> "Listo"
                                    else -> pedido.estado
                                },
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = if (pedido.estado == EstadoPedido.LISTO.name)
                                    "Recógelo en el casillero ${pedido.casillero ?: "C-07"}"
                                else
                                    "Entrega estimada: ${pedido.fechaEntrega}",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            StepperHorizontal(estadoActual = pedido.estado)
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No tienes pedidos activos",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Arma un pedido seleccionando tus servicios",
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Botón Nuevo Pedido
            item {
                Button(
                    onClick = onNuevoPedido,
                    colors = ButtonDefaults.buttonColors(containerColor = colorTurquesa),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Nuevo pedido", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Accesos Rápidos
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CardAccesoRapido(
                        titulo = "Mis pedidos",
                        subtitulo = "Ver estado",
                        icono = Icons.Outlined.ListAlt,
                        color = colorTurquesa,
                        modifier = Modifier.weight(1f),
                        onClick = onVerPedidos
                    )

                    CardAccesoRapido(
                        titulo = "Tarifas",
                        subtitulo = "Precios por kg",
                        icono = Icons.Outlined.LocalOffer,
                        color = colorTurquesa,
                        modifier = Modifier.weight(1f),
                        onClick = onVerTarifas
                    )
                }
            }

            // Sección Últimos Pedidos
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Últimos pedidos",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = "Ver todos",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorTurquesa,
                        modifier = Modifier.clickable { onVerPedidos() }
                    )
                }
            }

            items(recientes) { item ->
                TarjetaPedidoItem(
                    item = item,
                    colorTurquesa = colorTurquesa,
                    onClick = { onVerDetallePedido(item.pedido.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun CardAccesoRapido(
    titulo: String,
    subtitulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFCFFAFE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icono, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = subtitulo, fontSize = 12.sp, color = Color(0xFF64748B))
            }
        }
    }
}

@Composable
private fun TarjetaPedidoItem(
    item: PedidoConDetalle,
    colorTurquesa: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 1.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Pedido #${item.pedido.id.toString().padStart(3, '0')}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Text(
                    text = "${item.detalles.size} servicios · ${item.pedido.fechaRegistro}",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (item.pedido.estado) {
                        EstadoPedido.LISTO.name -> Color(0xFFDCFCE7)
                        EstadoPedido.ENTREGADO.name -> Color(0xFFF1F5F9)
                        else -> Color(0xFFFEF3C7)
                    }
                ) {
                    Text(
                        text = item.pedido.estado,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (item.pedido.estado) {
                            EstadoPedido.LISTO.name -> Color(0xFF16A34A)
                            EstadoPedido.ENTREGADO.name -> Color(0xFF475569)
                            else -> Color(0xFFD97706)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "S/ ${String.format("%.2f", item.pedido.total)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = colorTurquesa
                )
                if (item.pedido.estado == EstadoPedido.LISTO.name && item.pedido.casillero != null) {
                    Text(
                        text = "Casillero ${item.pedido.casillero}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF16A34A)
                    )
                }
            }
        }
    }
}

@Composable
private fun StepperHorizontal(estadoActual: String) {
    val estados = listOf(
        EstadoPedido.RECIBIDO.name,
        EstadoPedido.EN_PROCESO.name,
        EstadoPedido.LISTO.name,
        EstadoPedido.ENTREGADO.name
    )
    val idxActual = estados.indexOf(estadoActual).coerceAtLeast(0)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        estados.forEachIndexed { idx, _ ->
            val completado = idx <= idxActual
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (completado) Color.White else Color.White.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                if (completado) {
                    Text("✓", color = Color(0xFF0891B2), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            if (idx < estados.lastIndex) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .background(if (idx < idxActual) Color.White else Color.White.copy(alpha = 0.3f))
                )
            }
        }
    }
}