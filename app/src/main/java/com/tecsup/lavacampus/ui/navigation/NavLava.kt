package com.tecsup.lavacampus.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.lavacampus.ui.estudiante.LoginScreen
import com.tecsup.lavacampus.viewmodel.SesionViewModel

sealed class Destino(val ruta: String) {
    object Login : Destino("login")
    object EstudianteHome : Destino("estudiante_home")
    object AdminHome : Destino("admin_home")
}

@Composable
fun NavLava(
    navController: NavHostController = rememberNavController(),
    sesionViewModel: SesionViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Destino.Login.ruta
    ) {
        composable(Destino.Login.ruta) {
            LoginScreen(
                sesionViewModel = sesionViewModel,
                onLoginSuccess = { rol ->
                    val destino = if (rol == "ADMIN") Destino.AdminHome.ruta else Destino.EstudianteHome.ruta
                    navController.navigate(destino) {
                        popUpTo(Destino.Login.ruta) { inclusive = true }
                    }
                }
            )
        }

        composable(Destino.EstudianteHome.ruta) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFF0FCFF)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "¡Inicio de sesión exitoso!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF0891B2),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Perfil Activo: Estudiante (Paso 7)",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF0891B2)
                    )
                }
            }
        }

        composable(Destino.AdminHome.ruta) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFFAF8FF)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "¡Inicio de sesión exitoso!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFF6D28D9),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Perfil Activo: Administrador (Paso 11)",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF6D28D9)
                    )
                }
            }
        }
    }
}