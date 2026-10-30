package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Iron
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.model.EstadoPedido
import java.util.Locale

/** 40.5 -> "S/ 40.50" */
fun Double.soles(): String = "S/ " + String.format(Locale.US, "%.2f", this)

/** Código del pedido: 7 -> "#007" */
fun Int.codigoPedido(): String = "#" + this.toString().padStart(3, '0')

/** Ícono de Material para el campo `icono` del servicio (wash, iron, shirt). */
fun iconoServicio(icono: String): ImageVector = when (icono.lowercase()) {
    "wash" -> Icons.Filled.LocalLaundryService
    "iron" -> Icons.Filled.Iron
    else -> Icons.Filled.Checkroom
}

/** Tarjeta que usa los colores del tema (sirve para modo claro y oscuro). */
@Composable
fun TarjetaLava(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

/** Fila con título, texto de apoyo y un Switch. */
@Composable
fun FilaSwitch(
    titulo: String,
    descripcion: String? = null,
    marcado: Boolean,
    onCambio: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = titulo, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            if (descripcion != null) {
                Text(
                    text = descripcion,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(checked = marcado, onCheckedChange = onCambio)
    }
}

/** Línea de seguimiento vertical: Recibido, En proceso, Listo, Entregado. */
@Composable
fun StepperEstadoVertical(
    estadoActual: String,
    modifier: Modifier = Modifier
) {
    val etapas = listOf(
        EstadoPedido.RECIBIDO,
        EstadoPedido.EN_PROCESO,
        EstadoPedido.LISTO,
        EstadoPedido.ENTREGADO
    )
    val cancelado = estadoActual == EstadoPedido.CANCELADO.name
    val indice = etapas.indexOfFirst { it.name == estadoActual }
    val colorActivo = MaterialTheme.colorScheme.primary
    val colorInactivo = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)

    Column(modifier = modifier) {
        etapas.forEachIndexed { i, etapa ->
            val hecho = !cancelado && i <= indice
            Row(verticalAlignment = Alignment.Top) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(if (hecho) colorActivo else colorInactivo),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hecho) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    if (i < etapas.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(24.dp)
                                .background(if (!cancelado && i < indice) colorActivo else colorInactivo)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = etapa.etiqueta,
                    fontWeight = if (hecho && i == indice) FontWeight.Bold else FontWeight.Normal,
                    color = if (hecho) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        if (cancelado) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pedido cancelado",
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold
            )
        }
    }
}