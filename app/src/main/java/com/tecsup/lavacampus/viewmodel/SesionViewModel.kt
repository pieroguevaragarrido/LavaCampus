package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SesionViewModel(
    private val repository: LavaRepository,
    private val preferenciasStore: PreferenciasStore
) : ViewModel() {

    // Constructor secundario para compatibilidad si solo se pasa PreferenciasStore
    constructor(preferenciasStore: PreferenciasStore) : this(
        repository = error("LavaRepository es requerido"),
        preferenciasStore = preferenciasStore
    )

    val nombre: StateFlow<String?> = preferenciasStore.nombre
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val correo: StateFlow<String?> = preferenciasStore.correo
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val rol: StateFlow<String?> = preferenciasStore.rol
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val modoOscuro: StateFlow<Boolean> = preferenciasStore.modoOscuro
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun cambiarModoOscuro(activo: Boolean) {
        viewModelScope.launch {
            preferenciasStore.guardarModoOscuro(activo)
        }
    }

    fun cerrarSesion(onSuccess: () -> Unit) {
        viewModelScope.launch {
            preferenciasStore.cerrarSesion()
            onSuccess()
        }
    }
}