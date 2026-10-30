package com.tecsup.lavacampus.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

// ---------- Estudiante (turquesa) ----------
private val EstudianteClaro = lightColorScheme(
    primary = Color(0xFF0891B2),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCFFAFE),
    onPrimaryContainer = Color(0xFF083344),
    secondary = Color(0xFF475569),
    background = Color(0xFFF0FCFF),
    onBackground = Color(0xFF0F172A),
    surface = Color.White,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE0F2FE),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFF94A3B8)
)

private val EstudianteOscuro = darkColorScheme(
    primary = Color(0xFF22D3EE),
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF0E4F5C),
    onPrimaryContainer = Color(0xFFCFFAFE),
    secondary = Color(0xFFA9C4CC),
    background = Color(0xFF0B1A1F),
    onBackground = Color(0xFFE2F1F5),
    surface = Color(0xFF12262D),
    onSurface = Color(0xFFE2F1F5),
    surfaceVariant = Color(0xFF1D3640),
    onSurfaceVariant = Color(0xFFA9C4CC),
    outline = Color(0xFF5F7F89)
)

// ---------- Administrador (violeta) ----------
private val AdminClaro = lightColorScheme(
    primary = Color(0xFF6D28D9),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF2E1065),
    secondary = Color(0xFF5B21B6),
    background = Color(0xFFFAF8FF),
    onBackground = Color(0xFF1E1B2E),
    surface = Color.White,
    onSurface = Color(0xFF1E1B2E),
    surfaceVariant = Color(0xFFEDE9FE),
    onSurfaceVariant = Color(0xFF4C4570),
    outline = Color(0xFF9A93B8)
)

private val AdminOscuro = darkColorScheme(
    primary = Color(0xFFA78BFA),
    onPrimary = Color(0xFF2E1065),
    primaryContainer = Color(0xFF4C1D95),
    onPrimaryContainer = Color(0xFFEDE9FE),
    secondary = Color(0xFFC4B5FD),
    background = Color(0xFF14101F),
    onBackground = Color(0xFFEAE6F5),
    surface = Color(0xFF1D1730),
    onSurface = Color(0xFFEAE6F5),
    surfaceVariant = Color(0xFF2B2345),
    onSurfaceVariant = Color(0xFFC4BCDD),
    outline = Color(0xFF857CA3)
)

/**
 * Tema de la app según el perfil activo.
 * rol: "ADMIN" -> violeta; cualquier otro valor (CLIENTE o null) -> turquesa.
 */
@Composable
fun LavaTheme(
    rol: String?,
    oscuro: Boolean,
    content: @Composable () -> Unit
) {
    val esquema = when {
        rol == "ADMIN" && oscuro -> AdminOscuro
        rol == "ADMIN" -> AdminClaro
        oscuro -> EstudianteOscuro
        else -> EstudianteClaro
    }
    MaterialTheme(
        colorScheme = esquema,
        typography = Typography,
        content = content
    )
}

/**
 * Para las pantallas del estudiante que usan colores fijos (turquesa claro):
 * las mantiene siempre en claro y legibles aunque el modo oscuro esté activo.
 */
@Composable
fun PantallaClara(content: @Composable () -> Unit) {
    LavaTheme(rol = "CLIENTE", oscuro = false) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ) {
            content()
        }
    }
}