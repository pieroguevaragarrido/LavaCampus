package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.tecsup.lavacampus.viewmodel.MisPedidosViewModel
import java.util.Locale

@Composable
fun EstadoPedidoChipLocal(estado: String) {
    val (colorTexto, colorFondo) = when (estado) {
        "RECIBIDO" -> Color(0xFFD97706) to Color(0xFFFEF3C7)
        "EN_PROCESO" -> Color(0xFF2563EB) to Color(0xFFDBEAFE)
        "LISTO" -> Color(0xFF16A34A) to Color(0xFFDCFCE7)
        "ENTREGADO" -> Color(0xFF475569) to Color(0xFFF1F5F9)
        "CANCELADO" -> Color(0xFFDC2626) to Color(0xFFFEE2E2)
        else -> Color.Gray to Color.LightGray
    }

    Surface(
        color = colorFondo,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = estado.replace("_", " "),
            color = colorTexto,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun MisPedidosScreen(
    viewModel: MisPedidosViewModel,
    onPedidoClick: (Int) -> Unit,
    onNuevoPedido: () -> Unit = {},
    onRepetirPedido: (Int) -> Unit = {}
) {
    val activos by viewModel.activos.collectAsState()
    val historial by viewModel.historial.collectAsState()
    var pestañaSeleccionada by remember { mutableStateOf(0) }

    val colorTurquesa = Color(0xFF0891B2)
    val colorFondo = Color(0xFFF0FCFF)

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mis pedidos",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoPedido,
                containerColor = colorTurquesa,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo pedido")
            }
        },
        containerColor = colorFondo
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Pestañas Activos / Historial
            TabRow(
                selectedTabIndex = pestañaSeleccionada,
                containerColor = Color(0xFFE0F2FE),
                contentColor = colorTurquesa,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = pestañaSeleccionada == 0,
                    onClick = { pestañaSeleccionada = 0 },
                    text = { Text("Activos", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = pestañaSeleccionada == 1,
                    onClick = { pestañaSeleccionada = 1 },
                    text = { Text("Historial", fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val listaMostrar = if (pestañaSeleccionada == 0) activos else historial

            if (listaMostrar.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay pedidos registrados en esta sección.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listaMostrar, key = { it.pedido.id }) { item ->
                        ItemPedidoEstudiante(
                            pedidoConDetalle = item,
                            onClick = { onPedidoClick(item.pedido.id) },
                            onCancelar = { viewModel.cancelarPedido(item.pedido.id) },
                            onRepetir = { onRepetirPedido(item.pedido.id) }
                        )
                    }
                }
            }

            // Alerta si hay un pedido listo
            val pedidoListo = activos.firstOrNull { it.pedido.estado == EstadoPedido.LISTO.name }
            if (pedidoListo != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Aviso",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                        Text(
                            text = "Tu pedido #${pedidoListo.pedido.id} está listo: recógelo en el casillero ${pedidoListo.pedido.casillero ?: "indicado"}.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF92400E)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemPedidoEstudiante(
    pedidoConDetalle: PedidoConDetalle,
    onClick: () -> Unit,
    onCancelar: () -> Unit,
    onRepetir: () -> Unit = {}
) {
    val p = pedidoConDetalle.pedido
    val esListo = p.estado == EstadoPedido.LISTO.name
    val esRecibido = p.estado == EstadoPedido.RECIBIDO.name
    val esHistorial = p.estado == EstadoPedido.ENTREGADO.name || p.estado == EstadoPedido.CANCELADO.name

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .then(
                if (esListo) Modifier.border(2.dp, Color(0xFF16A34A), RoundedCornerShape(16.dp))
                else Modifier
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pedido #${p.id}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "S/ ${String.format(Locale.getDefault(), "%.2f", p.total)}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0891B2)
                    )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${pedidoConDetalle.detalles.size} servicio(s) · ${p.fechaRegistro}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EstadoPedidoChipLocal(estado = p.estado)

                if (esListo && !p.casillero.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF16A34A), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Casillero ${p.casillero}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                } else if (esRecibido) {
                    Text(
                        text = "Cancelar",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable { onCancelar() }
                    )
                } else if (esHistorial) {
                    Button(
                        onClick = onRepetir,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0891B2)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Pedir de nuevo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}