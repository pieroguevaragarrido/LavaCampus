package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.repository.LavaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ServiciosViewModel(
    private val repository: LavaRepository
) : ViewModel() {

    val servicios: StateFlow<List<Servicio>> = repository.observarServicios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val serviciosActivos: StateFlow<List<Servicio>> = repository.observarServiciosActivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    fun cambiarEstadoServicio(id: Int, activo: Boolean) {
        viewModelScope.launch {
            repository.cambiarEstadoServicio(id, activo)
        }
    }

    fun alternar(servicio: Servicio) {
        cambiarEstadoServicio(servicio.id, !servicio.activo)
    }

    /** Sirve para crear (id = 0) y para editar (id > 0). */
    fun guardar(servicio: Servicio, onListo: () -> Unit) {
        viewModelScope.launch {
            repository.guardarServicio(servicio)
            onListo()
        }
    }

    /** Un servicio que ya está en algún pedido NO se elimina: solo se desactiva. */
    fun eliminar(servicio: Servicio) {
        viewModelScope.launch {
            if (repository.servicioTieneDetalles(servicio.id)) {
                _mensaje.value = "No se puede eliminar: ya se usó en pedidos. Desactívalo."
            } else {
                repository.eliminarServicio(servicio)
                _mensaje.value = "Servicio eliminado"
            }
        }
    }
}