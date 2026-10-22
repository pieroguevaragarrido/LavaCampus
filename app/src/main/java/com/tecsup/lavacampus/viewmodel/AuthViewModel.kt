package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Usuario
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

    fun iniciarSesion(correo: String, contrasena: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _error.value = null
            val usuario: Usuario? = repository.login(correo, contrasena)
            if (usuario != null) {
                preferenciasStore.guardarSesion(
                    id = usuario.id,
                    usuarioId = usuario.id,
                    nombre = usuario.nombre,
                    correo = usuario.correo,
                    rol = usuario.rol
                )
                onSuccess(usuario.rol)
            } else {
                _error.value = "Correo o contraseña incorrectos"
            }
        }
    }
}