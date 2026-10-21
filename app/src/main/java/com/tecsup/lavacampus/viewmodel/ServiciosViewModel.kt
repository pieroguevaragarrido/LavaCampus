package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.repository.LavaRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ServiciosViewModel(
    private val repository: LavaRepository
) : ViewModel() {

    val servicios: StateFlow<List<Servicio>> = repository.observarServicios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val serviciosActivos: StateFlow<List<Servicio>> = repository.observarServiciosActivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cambiarEstadoServicio(id: Int, activo: Boolean) {
        viewModelScope.launch {
            repository.cambiarEstadoServicio(id, activo)
        }
    }

    fun guardarServicio(s: Servicio) {
        viewModelScope.launch {
            repository.guardarServicio(s)
        }
    }
}