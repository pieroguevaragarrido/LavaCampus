package com.tecsup.lavacampus.ui.estudiante

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import java.util.Locale

@Composable
fun PerfilScreen(
    viewModel: SesionViewModel,
    onCambiarPerfil: (String) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val nombre by viewModel.nombre.collectAsState()
    val correo by viewModel.correo.collectAsState()
    val codigo by viewModel.codigo.collectAsState()
    val modoOscuro by viewModel.modoOscuro.collectAsState()
    val notificaciones by viewModel.notificaciones.collectAsState()
    val activos by viewModel.pedidosActivos.collectAsState()
    val totales by viewModel.pedidosTotales.collectAsState()
    val kilos by viewModel.kilosLavados.collectAsState()

    // Surface: toma los colores del tema (claro u oscuro) también para el texto
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Mi perfil", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            // Datos del usuario
            TarjetaLava {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
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
                            text = nombre ?: "Estudiante",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = codigo ?: "",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = correo ?: "",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Contadores
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ContadorPerfil("Activos", activos.toString(), Modifier.weight(1f))
                ContadorPerfil("Pedidos", totales.toString(), Modifier.weight(1f))
                ContadorPerfil("Kilos", String.format(Locale.US, "%.1f", kilos), Modifier.weight(1f))
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
                    titulo = "Avisos",
                    descripcion = "Te avisamos cuando tu ropa esté lista",
                    marcado = notificaciones,
                    onCambio = { viewModel.cambiarNotificaciones(it) }
                )
            }

            OutlinedButton(
                onClick = { viewModel.cambiarPerfil("ADMIN", onCambiarPerfil) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Filled.SwapHoriz, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cambiar de perfil (Administrador)", fontWeight = FontWeight.Bold)
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
}

@Composable
private fun ContadorPerfil(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = titulo,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}