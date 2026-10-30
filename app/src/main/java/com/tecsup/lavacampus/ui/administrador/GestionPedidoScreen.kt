package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.model.Casilleros
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.components.StepperEstadoVertical
import com.tecsup.lavacampus.ui.components.TarjetaLava
import com.tecsup.lavacampus.ui.components.codigoPedido
import com.tecsup.lavacampus.ui.components.soles
import com.tecsup.lavacampus.ui.estudiante.EstadoPedidoChipLocal
import com.tecsup.lavacampus.viewmodel.GestionPedidoViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GestionPedidoScreen(
    pedidoId: Int,
    viewModel: GestionPedidoViewModel,
    onVolver: () -> Unit
) {
    LaunchedEffect(pedidoId) { viewModel.cargar(pedidoId) }

    val item by viewModel.pedido.collectAsState()
    val ocupados by viewModel.casillerosOcupados.collectAsState()
    val elegido by viewModel.casilleroElegido.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    val snackbar = remember { SnackbarHostState() }
    var confirmarCancelar by remember { mutableStateOf(false) }

    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbar.showSnackbar(it)
            viewModel.limpiarMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Pedido ${pedidoId.codigoPedido()}",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbar) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val actual = item
        if (actual == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val p = actual.pedido
            val estado = EstadoPedido.valueOf(p.estado)
            val siguiente = estado.siguiente()
            val esFinal = estado == EstadoPedido.ENTREGADO || estado == EstadoPedido.CANCELADO
            val puedeCancelar = estado == EstadoPedido.RECIBIDO || estado == EstadoPedido.EN_PROCESO

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Datos del estudiante
                item {
                    TarjetaLava {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = actual.usuario.nombre,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            EstadoPedidoChipLocal(estado = p.estado)
                        }
                        Text(
                            text = "${actual.usuario.codigo} · ${actual.usuario.correo}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Registro: ${p.fechaRegistro}  ·  Entrega: ${p.fechaEntrega}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (p.nota.isNotBlank()) {
                            Text(
                                text = "Nota: ${p.nota}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Seguimiento
                item {
                    TarjetaLava {
                        Text(
                            text = "Seguimiento",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        StepperEstadoVertical(estadoActual = p.estado)
                    }
                }

                // Casillero
                if (siguiente == EstadoPedido.LISTO) {
                    item {
                        TarjetaLava {
                            Text(
                                text = "Elige un casillero",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Los casilleros en gris ya están ocupados",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Casilleros.todos.forEach { c ->
                                    val ocupado = c in ocupados && c != p.casillero
                                    FilterChip(
                                        selected = elegido == c,
                                        onClick = { viewModel.elegirCasillero(c) },
                                        enabled = !ocupado,
                                        label = { Text(c, fontSize = 12.sp) }
                                    )
                                }
                            }
                        }
                    }
                } else if (estado == EstadoPedido.LISTO && p.casillero != null) {
                    item {
                        TarjetaLava {
                            Text(
                                text = "Casillero asignado",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = p.casillero,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold,
                                color = EstadoPedido.LISTO.color
                            )
                        }
                    }
                }

                // Líneas, total y cobro
                item {
                    TarjetaLava {
                        actual.detalles.forEach { d ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${d.servicio.nombre} (${d.detalle.cantidad} × ${d.detalle.precioUnitario.soles()})",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = d.detalle.subtotal.soles(),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = p.total.soles(),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Cobrado",
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Switch(
                                checked = p.pagado,
                                enabled = estado != EstadoPedido.CANCELADO,
                                onCheckedChange = { viewModel.marcarPagado(it) }
                            )
                        }
                    }
                }

                // Recordatorio de la regla
                if (!esFinal) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Regla: para pasar a Listo elige un casillero libre; " +
                                        "para Entregar el pedido debe estar cobrado.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Acciones
                if (siguiente != null) {
                    item {
                        Button(
                            onClick = { viewModel.avanzar() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "Marcar como ${siguiente.etiqueta}",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (puedeCancelar) {
                    item {
                        OutlinedButton(
                            onClick = { confirmarCancelar = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = "Cancelar pedido",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    if (confirmarCancelar) {
        AlertDialog(
            onDismissRequest = { confirmarCancelar = false },
            title = { Text("¿Cancelar pedido?") },
            text = { Text("El pedido pasará a CANCELADO y no se podrá revertir.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmarCancelar = false
                        viewModel.cancelar(onListo = onVolver)
                    }
                ) {
                    Text("Sí, cancelar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarCancelar = false }) { Text("Volver") }
            }
        )
    }
}