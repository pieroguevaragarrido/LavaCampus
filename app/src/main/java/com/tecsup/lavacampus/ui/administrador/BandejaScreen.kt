package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.components.codigoPedido
import com.tecsup.lavacampus.ui.components.soles
import com.tecsup.lavacampus.ui.estudiante.EstadoPedidoChipLocal
import com.tecsup.lavacampus.viewmodel.BandejaViewModel
import com.tecsup.lavacampus.viewmodel.FiltroBandeja

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandejaScreen(
    viewModel: BandejaViewModel,
    onAbrirPedido: (Int) -> Unit
) {
    val pedidos by viewModel.pedidos.collectAsState()
    val filtro by viewModel.filtro.collectAsState()
    val busqueda by viewModel.busqueda.collectAsState()
    val idsAtrasados by viewModel.idsAtrasados.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Pedidos",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = busqueda,
            onValueChange = { viewModel.buscar(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar por estudiante o número") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(FiltroBandeja.values().toList()) { f ->
                FilterChip(
                    selected = filtro == f,
                    onClick = { viewModel.cambiarFiltro(f) },
                    label = { Text(f.etiqueta) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (pedidos.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No hay pedidos con ese filtro",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pedidos, key = { it.pedido.id }) { item ->
                    TarjetaBandeja(
                        item = item,
                        esAtrasado = item.pedido.id in idsAtrasados,
                        onAbrir = { onAbrirPedido(item.pedido.id) },
                        onIniciar = { viewModel.iniciar(item) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TarjetaBandeja(
    item: PedidoConDetalle,
    esAtrasado: Boolean,
    onAbrir: () -> Unit,
    onIniciar: () -> Unit
) {
    val p = item.pedido
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAbrir() },
        shape = RoundedCornerShape(16.dp),
        border = if (esAtrasado) BorderStroke(1.dp, MaterialTheme.colorScheme.error) else null,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "${p.id.codigoPedido()} · ${item.usuario.nombre}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (esAtrasado) {
                        Text(
                            text = "Atrasado",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                EstadoPedidoChipLocal(estado = p.estado)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.detalles.joinToString(", ") { it.servicio.nombre },
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = p.total.soles(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (p.pagado) "Cobrado" else "Pago pendiente",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                when (p.estado) {
                    EstadoPedido.RECIBIDO.name ->
                        Button(onClick = onIniciar) { Text("Iniciar") }

                    EstadoPedido.EN_PROCESO.name ->
                        Button(onClick = onAbrir) { Text("Marcar listo") }

                    EstadoPedido.LISTO.name ->
                        Button(onClick = onAbrir) { Text("Entregar") }

                    else -> Unit
                }
            }
        }
    }
}