package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.DryCleaning
import androidx.compose.material.icons.filled.Iron
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.viewmodel.MisPedidosViewModel
import java.util.Locale

fun Servicio.textoPrecio(): String {
    val unidadTexto = when (unidad) {
        "KG" -> "por kg"
        "PRENDA" -> "por prenda"
        "UNIDAD" -> "por unidad"
        else -> "por $unidad"
    }
    return "S/ ${String.format(Locale.getDefault(), "%.2f", precio)} $unidadTexto"
}

@Composable
fun IconoServicioLocal(tipo: String, modifier: Modifier = Modifier) {
    val icono = when (tipo.lowercase()) {
        "wash", "lavado" -> Icons.Default.LocalLaundryService
        "iron", "planchado" -> Icons.Default.Iron
        "dry", "seco" -> Icons.Default.DryCleaning
        else -> Icons.Default.Checkroom
    }
    Icon(
        imageVector = icono,
        contentDescription = tipo,
        tint = Color(0xFF0891B2),
        modifier = modifier
    )
}

@Composable
fun TarifasScreen(viewModel: MisPedidosViewModel) {
    val servicios by viewModel.serviciosActivos.collectAsState()
    val colorFondo = Color(0xFFF0FCFF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo)
            .padding(16.dp)
    ) {
        Text(
            text = "Tarifas y servicios",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(servicios, key = { it.id }) { s ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconoServicioLocal(
                            tipo = s.icono,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = s.nombre,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = s.textoPrecio(),
                                color = Color(0xFF0891B2),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Entrega en ${s.horasEntrega} h",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horario e información
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Horario de atención", fontWeight = FontWeight.Bold, color = Color(0xFF0369A1))
                Text("Lunes a sábado: 8:00 a 18:00\nRecojo en casilleros de pabellón A", fontSize = 13.sp, color = Color(0xFF075985))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Mínimo 1 kg por servicio de lavado.\nMáximo 3 pedidos activos por estudiante.", fontSize = 12.sp, color = Color(0xFF92400E))
            }
        }
    }
}