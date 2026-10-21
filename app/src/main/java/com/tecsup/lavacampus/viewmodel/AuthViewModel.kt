package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: LavaRepository,
    private val preferenciasStore: PreferenciasStore
) : ViewModel() {

    val usuarioId: StateFlow<Int?> = preferenciasStore.usuarioId.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    val rol: StateFlow<String?> = preferenciasStore.rol.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    val nombre: StateFlow<String?> = preferenciasStore.nombre.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    val correo: StateFlow<String?> = preferenciasStore.correo.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), null
    )

    val modoOscuro: StateFlow<Boolean> = preferenciasStore.modoOscuro.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), false
    )

    private val _errorLogin = MutableStateFlow<String?>(null)
    val errorLogin: StateFlow<String?> = _errorLogin.asStateFlow()

    fun login(correoInput: String, claveInput: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _errorLogin.value = null
            val usuario = repository.login(correoInput.trim(), claveInput.trim())
            if (usuario != null) {
                preferenciasStore.guardarSesion(
                    id = usuario.id,
                    nombre = usuario.nombre,
                    correo = usuario.correo,
                    rol = usuario.rol
                )
                onSuccess()
            } else {
                _errorLogin.value = "Correo o contraseña incorrectos"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            preferenciasStore.cerrarSesion()
        }
    }

    fun toggleModoOscuro(activo: Boolean) {
        viewModelScope.launch {
            preferenciasStore.guardarModoOscuro(activo)
        }
    }
}