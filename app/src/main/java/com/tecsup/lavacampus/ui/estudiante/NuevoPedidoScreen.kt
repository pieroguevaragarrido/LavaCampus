package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoPedidoScreen(
    nuevoPedidoViewModel: NuevoPedidoViewModel,
    onVolver: () -> Unit,
    onPedidoCreado: () -> Unit
) {
    val servicios by nuevoPedidoViewModel.serviciosActivos.collectAsState()
    val cantidades by nuevoPedidoViewModel.cantidades.collectAsState()
    val nota by nuevoPedidoViewModel.nota.collectAsState()
    val mensajeError by nuevoPedidoViewModel.mensajeError.collectAsState()
    val total by nuevoPedidoViewModel.total.collectAsState()
    val fechaEntrega by nuevoPedidoViewModel.fechaEntrega.collectAsState()

    val colorTurquesa = Color(0xFF0891B2)
    val colorFondo = Color(0xFFF0FCFF)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo pedido", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "¿Qué necesitas?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            items(servicios) { servicio ->
                val cantidad = cantidades[servicio.id] ?: 0.0
                ItemServicioSelector(
                    servicio = servicio,
                    cantidad = cantidad,
                    colorTurquesa = colorTurquesa,
                    onCambiarCantidad = { delta ->
                        nuevoPedidoViewModel.cambiarCantidad(servicio, delta)
                    }
                )
            }

            item {
                OutlinedTextField(
                    value = nota,
                    onValueChange = { nuevoPedidoViewModel.cambiarNota(it) },
                    label = { Text("Nota (opcional)") },
                    placeholder = { Text("Ej: Ropa de color separada") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFCFFAFE)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Resumen",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = colorTurquesa
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val seleccionados = cantidades.filter { it.value > 0 }
                        if (seleccionados.isEmpty()) {
                            Text(
                                text = "Selecciona al menos un servicio",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        } else {
                            seleccionados.forEach { (servicioId, cant) ->
                                val s = servicios.find { it.id == servicioId }
                                if (s != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        val textCant = if (s.unidad == "KG") "$cant kg" else "${cant.toInt()} ${s.unidad.lowercase()}s"
                                        Text(
                                            text = "${s.nombre}: $textCant",
                                            fontSize = 14.sp,
                                            color = Color(0xFF334155)
                                        )
                                        Text(
                                            text = "S/ ${String.format("%.2f", s.precio * cant)}",
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF334155)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = colorTurquesa.copy(alpha = 0.3f)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Total",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "S/ ${String.format("%.2f", total)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = colorTurquesa
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Entrega estimada: $fechaEntrega",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }

            if (mensajeError != null) {
                item {
                    Text(
                        text = mensajeError!!,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item {
                Button(
                    onClick = {
                        nuevoPedidoViewModel.confirmarPedido(onSuccess = onPedidoCreado)
                    },
                    enabled = total > 0.0,
                    colors = ButtonDefaults.buttonColors(containerColor = colorTurquesa),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Confirmar pedido", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun ItemServicioSelector(
    servicio: Servicio,
    cantidad: Double,
    colorTurquesa: Color,
    onCambiarCantidad: (Double) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (cantidad > 0) 2.dp else 1.dp,
                color = if (cantidad > 0) colorTurquesa else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = servicio.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0F172A)
                )
                Text(
                    text = "S/ ${String.format("%.2f", servicio.precio)} por ${servicio.unidad.lowercase()} · ${servicio.horasEntrega} h",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (cantidad > 0) colorTurquesa else Color(0xFFE2E8F0))
                        .clickable {
                            val paso = if (servicio.unidad == "KG") 0.5 else 1.0
                            onCambiarCantidad(-paso)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Menos",
                        tint = if (cantidad > 0) Color.White else Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = if (servicio.unidad == "KG") "$cantidad kg" else "${cantidad.toInt()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(colorTurquesa)
                        .clickable {
                            val paso = if (servicio.unidad == "KG") 0.5 else 1.0
                            onCambiarCantidad(paso)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Más",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}