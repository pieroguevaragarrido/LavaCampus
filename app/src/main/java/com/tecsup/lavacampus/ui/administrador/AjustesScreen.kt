package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.ui.components.FilaSwitch
import com.tecsup.lavacampus.ui.components.TarjetaLava
import com.tecsup.lavacampus.viewmodel.SesionViewModel

@Composable
fun AjustesScreen(
    viewModel: SesionViewModel,
    onCambiarPerfil: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val nombre by viewModel.nombre.collectAsState()
    val correo by viewModel.correo.collectAsState()
    val modoOscuro by viewModel.modoOscuro.collectAsState()
    val notificaciones by viewModel.notificaciones.collectAsState()
    val recibirPedidos by viewModel.recibirPedidos.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ajustes",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Datos del usuario
        TarjetaLava {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = nombre ?: "Administración",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = correo ?: "",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Perfil: Administrador",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Preferencias
        TarjetaLava {
            FilaSwitch(
                titulo = "Modo oscuro",
                marcado = modoOscuro,
                onCambio = { viewModel.cambiarModoOscuro(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            FilaSwitch(
                titulo = "Notificaciones",
                descripcion = "Avisos de nuevos pedidos",
                marcado = notificaciones,
                onCambio = { viewModel.cambiarNotificaciones(it) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            FilaSwitch(
                titulo = "Recibir pedidos",
                descripcion = "Si lo apagas, los estudiantes no podrán crear pedidos",
                marcado = recibirPedidos,
                onCambio = { viewModel.cambiarRecibirPedidos(it) }
            )
        }

        OutlinedButton(
            onClick = { viewModel.cambiarPerfil("CLIENTE", onCambiarPerfil) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(Icons.Filled.SwapHoriz, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cambiar de perfil (Estudiante)", fontWeight = FontWeight.Bold)
        }

        Button(
            onClick = { viewModel.cerrarSesion(onCerrarSesion) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar sesión", fontWeight = FontWeight.Bold)
        }
    }
}