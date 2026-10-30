package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.viewmodel.DetallePedidoViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePedidoScreen(
    pedidoId: Int,
    detalleViewModel: DetallePedidoViewModel,
    onVolver: () -> Unit
) {
    LaunchedEffect(pedidoId) {
        detalleViewModel.cargarPedido(pedidoId)
    }

    val pedidoConDetalle by detalleViewModel.pedidoDetalle.collectAsState()
    var mostrarDialogoCancelar by remember { mutableStateOf(false) }

    val colorTurquesa = Color(0xFF0891B2)
    val colorFondo = Color(0xFFF0FCFF)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Pedido #${pedidoId.toString().padStart(3, '0')}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorFondo)
            )
        },
        containerColor = colorFondo
    ) { padding ->
        val item = pedidoConDetalle
        if (item == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorTurquesa)
            }
        } else {
            val pedido = item.pedido
            val esCancelable = pedido.estado == EstadoPedido.RECIBIDO.name

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(4.dp)) }

                // Tarjeta de Estado y Casillero
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Estado actual",
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = when (pedido.estado) {
                                        EstadoPedido.LISTO.name -> Color(0xFFDCFCE7)
                                        EstadoPedido.ENTREGADO.name -> Color(0xFFF1F5F9)
                                        EstadoPedido.CANCELADO.name -> Color(0xFFFEE2E2)
                                        else -> Color(0xFFFEF3C7)
                                    }
                                ) {
                                    Text(
                                        text = pedido.estado,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (pedido.estado) {
                                            EstadoPedido.LISTO.name -> Color(0xFF16A34A)
                                            EstadoPedido.ENTREGADO.name -> Color(0xFF475569)
                                            EstadoPedido.CANCELADO.name -> Color(0xFFDC2626)
                                            else -> Color(0xFFD97706)
                                        },
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (pedido.estado == EstadoPedido.LISTO.name && pedido.casillero != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(Color(0xFFDCFCE7), RoundedCornerShape(12.dp))
                                        .padding(16.dp)
                                ) {
                                    Column {
                                        Text(
                                            text = "Casillero Asignado",
                                            fontSize = 12.sp,
                                            color = Color(0xFF15803D)
                                        )
                                        Text(
                                            text = pedido.casillero,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF16A34A)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Fecha de registro", fontSize = 12.sp, color = Color(0xFF64748B))
                                    Text(pedido.fechaRegistro, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Fecha de entrega", fontSize = 12.sp, color = Color(0xFF64748B))
                                    Text(pedido.fechaEntrega, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

                // Desglose de Servicios
                item {
                    Text(
                        text = "Servicios contratados",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                // items(...) nativo sobre List<DetalleConServicio>
                items(item.detalles) { d ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 1.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = d.servicio.nombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "${d.detalle.cantidad} x S/ ${String.format(Locale.US, "%.2f", d.detalle.precioUnitario)}",
                                    fontSize = 13.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            Text(
                                text = "S/ ${String.format(Locale.US, "%.2f", d.detalle.subtotal)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = colorTurquesa
                            )
                        }
                    }
                }

                if (pedido.nota.isNotBlank()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Nota del pedido",
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = pedido.nota,
                                    fontSize = 14.sp,
                                    color = Color(0xFF334155)
                                )
                            }
                        }
                    }
                }

                // Resumen de Pago
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFCFFAFE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            if (pedido.descuento > 0.0) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Descuento por volumen",
                                        fontSize = 14.sp,
                                        color = Color(0xFF16A34A)
                                    )
                                    Text(
                                        text = "-S/ ${String.format(Locale.US, "%.2f", pedido.descuento)}",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF16A34A)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total pagado",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "S/ ${String.format(Locale.US, "%.2f", pedido.total)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    color = colorTurquesa
                                )
                            }
                        }
                    }
                }

                // Botón Cancelar Pedido
                if (esCancelable) {
                    item {
                        OutlinedButton(
                            onClick = { mostrarDialogoCancelar = true },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text("Cancelar pedido", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }

        if (mostrarDialogoCancelar) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoCancelar = false },
                title = { Text("¿Cancelar pedido?") },
                text = { Text("Esta acción no se puede deshacer. El pedido cambiará a estado CANCELADO.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            mostrarDialogoCancelar = false
                            detalleViewModel.cancelarPedido(onSuccess = onVolver)
                        }
                    ) {
                        Text("Sí, cancelar", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarDialogoCancelar = false }) {
                        Text("Volver")
                    }
                }
            )
        }
    }
}