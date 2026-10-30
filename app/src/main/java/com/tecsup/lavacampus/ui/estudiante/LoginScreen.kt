package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tecsup.lavacampus.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (String) -> Unit
) {
    var rolSeleccionado by remember { mutableStateOf("CLIENTE") }
    var correo by remember { mutableStateOf("ana@correo.test") }
    var clave by remember { mutableStateOf("1234") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val errorVm by viewModel.error.collectAsState()

    val esEstudiante = rolSeleccionado == "CLIENTE"
    val colorPrimario = if (esEstudiante) Color(0xFF0891B2) else Color(0xFF6D28D9)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Bienvenido",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B),
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "Ingresa para continuar",
            fontSize = 15.sp,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Selector Estudiante / Administrador
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50.dp))
                .background(Color(0xFFF1F5F9))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50.dp))
                    .background(if (esEstudiante) Color(0xFF0891B2) else Color.Transparent)
                    .clickable {
                        rolSeleccionado = "CLIENTE"
                        correo = "ana@correo.test"
                        clave = "1234"
                        errorMsg = null
                        viewModel.limpiarError()
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Estudiante",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (esEstudiante) Color.White else Color(0xFF64748B)
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50.dp))
                    .background(if (!esEstudiante) Color(0xFF6D28D9) else Color.Transparent)
                    .clickable {
                        rolSeleccionado = "ADMIN"
                        correo = "admin@lavacampus.test"
                        clave = "admin"
                        errorMsg = null
                        viewModel.limpiarError()
                    }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Administrador",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = if (!esEstudiante) Color.White else Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Campo Correo
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") },
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null, tint = colorPrimario) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorPrimario,
                focusedLabelColor = colorPrimario
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo Contraseña
        OutlinedTextField(
            value = clave,
            onValueChange = { clave = it },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null, tint = colorPrimario) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorPrimario,
                focusedLabelColor = colorPrimario
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            color = colorPrimario,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { }
        )

        Spacer(modifier = Modifier.height(24.dp))

        (errorMsg ?: errorVm)?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Button(
            onClick = {
                if (correo.isBlank() || clave.isBlank()) {
                    errorMsg = "Completa todos los campos"
                } else {
                    errorMsg = null
                    viewModel.login(correo, clave, rolSeleccionado, onLoginSuccess)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorPrimario)
        ) {
            Text("Ingresar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (esEstudiante) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿No tienes cuenta? ", color = Color.Gray, fontSize = 13.sp)
                Text(
                    "Regístrate",
                    color = colorPrimario,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { }
                )
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E8FF)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Modo práctica",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6D28D9),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Puedes cambiar de perfil desde Ajustes para probar ambos roles.",
                        fontSize = 12.sp,
                        color = Color(0xFF5B21B6)
                    )
                }
            }
        }
    }
}