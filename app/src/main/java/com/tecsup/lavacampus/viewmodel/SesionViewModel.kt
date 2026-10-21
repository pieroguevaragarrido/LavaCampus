package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SesionViewModel(
    private val repository: LavaRepository,
    private val preferenciasStore: PreferenciasStore
) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val modoOscuro: StateFlow<Boolean> = preferenciasStore.modoOscuro.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    fun login(correo: String, clave: String, rolSeleccionado: String, onSuccess: (String) -> Unit) {
        val correoTrim = correo.trim()
        val claveTrim = clave.trim()

        if (correoTrim.isEmpty() || claveTrim.isEmpty()) {
            _error.value = "Ingresa tu correo y contraseña"
            return
        }

        viewModelScope.launch {
            _error.value = null
            val usuario = repository.login(correoTrim, claveTrim)
            if (usuario == null) {
                _error.value = "Correo o contraseña incorrectos"
            } else if (usuario.rol != rolSeleccionado) {
                _error.value = "Esta cuenta es de tipo ${if (usuario.rol == "CLIENTE") "Estudiante" else "Administrador"}. Selecciona el perfil correcto."
            } else {
                _usuarioActual.value = usuario
                preferenciasStore.guardarSesion(
                    id = usuario.id,
                    nombre = usuario.nombre,
                    correo = usuario.correo,
                    rol = usuario.rol
                )
                onSuccess(usuario.rol)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _usuarioActual.value = null
            preferenciasStore.cerrarSesion()
        }
    }
}