package com.tecsup.lavacampus.ui.components.graficos

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Un dato para los gráficos: etiqueta, valor y color. */
data class DatoGrafico(
    val etiqueta: String,
    val valor: Float,
    val color: Color
)

@Composable
fun GraficoDona(
    datos: List<DatoGrafico>,
    modifier: Modifier = Modifier
) {
    val total = datos.sumOf { it.valor.toDouble() }.toFloat()
    val colorVacio = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(130.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val grosor = 22.dp.toPx()
                val diametro = size.minDimension - grosor
                val esquina = Offset((size.width - diametro) / 2f, (size.height - diametro) / 2f)
                val tamano = Size(diametro, diametro)

                if (total <= 0f) {
                    drawArc(
                        color = colorVacio,
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = esquina,
                        size = tamano,
                        style = Stroke(width = grosor)
                    )
                } else {
                    var inicio = -90f
                    datos.forEach { d ->
                        if (d.valor > 0f) {
                            val barrido = d.valor / total * 360f
                            drawArc(
                                color = d.color,
                                startAngle = inicio,
                                sweepAngle = barrido,
                                useCenter = false,
                                topLeft = esquina,
                                size = tamano,
                                style = Stroke(width = grosor)
                            )
                            inicio += barrido
                        }
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = total.toInt().toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "pedidos",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(20.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            datos.forEach { d ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(d.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${d.etiqueta}: ${d.valor.toInt()}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
fun GraficoBarras(
    datos: List<DatoGrafico>,
    modifier: Modifier = Modifier
) {
    val maximo = (datos.maxOfOrNull { it.valor } ?: 0f).coerceAtLeast(1f)

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            datos.forEach { d ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = "S/ ${d.valor.toInt()}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val altura = (110f * d.valor / maximo).coerceAtLeast(3f)
                    Canvas(
                        modifier = Modifier
                            .width(30.dp)
                            .height(altura.dp)
                    ) {
                        drawRoundRect(
                            color = d.color,
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            datos.forEach { d ->
                Text(
                    text = d.etiqueta,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 11.sp,
                    maxLines = 2,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}