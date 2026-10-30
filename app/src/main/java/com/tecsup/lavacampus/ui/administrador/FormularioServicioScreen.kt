package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.ui.components.FilaSwitch
import com.tecsup.lavacampus.ui.components.iconoServicio
import com.tecsup.lavacampus.viewmodel.ServiciosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioServicioScreen(
    servicioId: Int,
    viewModel: ServiciosViewModel,
    onVolver: () -> Unit
) {
    // id = 0 -> crear; id > 0 -> editar
    val servicios by viewModel.servicios.collectAsState()
    val existente: Servicio? = remember(servicioId) {
        if (servicioId == 0) null else viewModel.servicios.value.find { it.id == servicioId }
    }

    var nombre by remember { mutableStateOf(existente?.nombre ?: "") }
    var unidad by remember { mutableStateOf(existente?.unidad ?: "KG") }
    var precioTexto by remember {
        mutableStateOf(existente?.precio?.let { String.format(java.util.Locale.US, "%.2f", it) } ?: "")
    }
    var horas by remember { mutableStateOf(existente?.horasEntrega ?: 24) }
    var descripcion by remember { mutableStateOf(existente?.descripcion ?: "") }
    var icono by remember { mutableStateOf(existente?.icono ?: "wash") }
    var activo by remember { mutableStateOf(existente?.activo ?: true) }
    var intentoGuardar by remember { mutableStateOf(false) }

    val precio = precioTexto.replace(',', '.').toDoubleOrNull()
    val nombreValido = nombre.isNotBlank()
    val precioValido = precio != null && precio > 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (servicioId == 0) "Nuevo servicio" else "Editar servicio",
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
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                isError = intentoGuardar && !nombreValido,
                supportingText = {
                    if (intentoGuardar && !nombreValido) Text("El nombre es obligatorio")
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Se cobra por", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("KG" to "Kilo", "PRENDA" to "Prenda", "UNIDAD" to "Unidad").forEach { (valor, etiqueta) ->
                    FilterChip(
                        selected = unidad == valor,
                        onClick = { unidad = valor },
                        label = { Text(etiqueta) }
                    )
                }
            }

            OutlinedTextField(
                value = precioTexto,
                onValueChange = { precioTexto = it },
                label = { Text("Precio (S/)") },
                isError = intentoGuardar && !precioValido,
                supportingText = {
                    if (intentoGuardar && !precioValido) Text("El precio debe ser mayor que 0")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Tiempo de entrega", fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(24, 48, 72).forEach { h ->
                    FilterChip(
                        selected = horas == h,
                        onClick = { horas = h },
                        label = { Text("$h h") }
                    )
                }
            }

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Text("Ícono", fontWeight = FontWeight.SemiBold)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("wash" to "Lavado", "iron" to "Plancha", "shirt" to "Camisa").forEach { (valor, etiqueta) ->
                    FilterChip(
                        selected = icono == valor,
                        onClick = { icono = valor },
                        label = { Text(etiqueta) },
                        leadingIcon = {
                            Icon(
                                imageVector = iconoServicio(valor),
                                contentDescription = null,
                                modifier = Modifier.padding(0.dp)
                            )
                        }
                    )
                }
            }

            FilaSwitch(
                titulo = "Servicio activo",
                descripcion = "Si está inactivo, no aparece al hacer un pedido",
                marcado = activo,
                onCambio = { activo = it }
            )

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    intentoGuardar = true
                    if (nombreValido && precioValido && precio != null) {
                        val servicio = Servicio(
                            id = existente?.id ?: 0,
                            nombre = nombre.trim(),
                            descripcion = descripcion.trim(),
                            unidad = unidad,
                            precio = precio,
                            horasEntrega = horas,
                            activo = activo,
                            icono = icono
                        )
                        viewModel.guardar(servicio, onListo = onVolver)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (servicioId == 0) "Crear servicio" else "Guardar cambios",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}