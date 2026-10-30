package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.components.TarjetaLava
import com.tecsup.lavacampus.ui.components.graficos.GraficoBarras
import com.tecsup.lavacampus.ui.components.graficos.GraficoDona
import com.tecsup.lavacampus.ui.components.soles
import com.tecsup.lavacampus.viewmodel.PanelViewModel

@Composable
fun PanelScreen(
    viewModel: PanelViewModel,
    onVerListos: () -> Unit
) {
    val pedidosHoy by viewModel.pedidosHoy.collectAsState()
    val enProceso by viewModel.enProceso.collectAsState()
    val porRecoger by viewModel.porRecoger.collectAsState()
    val ingresosSemana by viewModel.ingresosSemana.collectAsState()
    val porEstado by viewModel.porEstado.collectAsState()
    val porServicio by viewModel.porServicio.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Panel de la lavandería",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Resumen de hoy y de la semana",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // 3 contadores de color
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ContadorPanel(
                    titulo = "Pedidos hoy",
                    valor = pedidosHoy,
                    color = EstadoPedido.RECIBIDO.color,
                    modifier = Modifier.weight(1f)
                )
                ContadorPanel(
                    titulo = "En proceso",
                    valor = enProceso,
                    color = EstadoPedido.EN_PROCESO.color,
                    modifier = Modifier.weight(1f)
                )
                ContadorPanel(
                    titulo = "Por recoger",
                    valor = porRecoger,
                    color = EstadoPedido.LISTO.color,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Tarjeta violeta de ingresos
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Ingresos de la semana", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = ingresosSemana.soles(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "Solo pedidos cobrados desde el lunes", fontSize = 12.sp)
                }
            }
        }

        // Alerta si hay pedidos LISTO esperando recojo
        if (porRecoger > 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = EstadoPedido.LISTO.color.copy(alpha = 0.15f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = null,
                            tint = EstadoPedido.LISTO.color
                        )
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 12.dp)
                        ) {
                            Text(
                                text = "Pedidos esperando recojo",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (porRecoger == 1) "1 pedido está listo en casillero"
                                else "$porRecoger pedidos están listos en casillero",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Button(
                            onClick = onVerListos,
                            colors = ButtonDefaults.buttonColors(containerColor = EstadoPedido.LISTO.color)
                        ) {
                            Text("Ver", color = Color.White)
                        }
                    }
                }
            }
        }

        // Dona: pedidos por estado
        item {
            TarjetaLava {
                Text(
                    text = "Pedidos por estado",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                GraficoDona(datos = porEstado)
            }
        }

        // Barras: ingresos por servicio
        item {
            TarjetaLava {
                Text(
                    text = "Ingresos por servicio (semana)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (porServicio.isEmpty()) {
                    Text(
                        text = "Aún no hay pedidos cobrados esta semana",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    GraficoBarras(datos = porServicio)
                }
            }
        }
    }
}

@Composable
private fun ContadorPanel(
    titulo: String,
    valor: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = valor.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = titulo,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}