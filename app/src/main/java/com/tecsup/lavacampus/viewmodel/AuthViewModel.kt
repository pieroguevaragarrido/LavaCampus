package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: LavaRepository,
    private val preferenciasStore: PreferenciasStore
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun limpiarError() {
        _error.value = null
    }

    fun login(
        correo: String,
        contrasena: String,
        rolSeleccionado: String,
        onSuccess: (String) -> Unit
    ) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _error.value = "Completa todos los campos"
            return
        }
        viewModelScope.launch {
            _error.value = null
            val usuario = repository.login(correo.trim(), contrasena)
            when {
                usuario == null -> {
                    _error.value = "Correo o contraseña incorrectos"
                }
                usuario.rol != rolSeleccionado -> {
                    _error.value = if (rolSeleccionado == "ADMIN") {
                        "Esta cuenta no es de administrador"
                    } else {
                        "Esta cuenta no es de estudiante"
                    }
                }
                else -> {
                    preferenciasStore.guardarSesion(
                        id = usuario.id,
                        usuarioId = usuario.id,
                        nombre = usuario.nombre,
                        correo = usuario.correo,
                        rol = usuario.rol,
                        codigo = usuario.codigo
                    )
                    onSuccess(usuario.rol)
                }
            }
        }
    }
}