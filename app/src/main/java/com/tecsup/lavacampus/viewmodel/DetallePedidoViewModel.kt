package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DetallePedidoViewModel(
    private val repository: LavaRepository
) : ViewModel() {

    private val _pedidoId = MutableStateFlow<Int?>(null)

    val pedidoDetalle: StateFlow<PedidoConDetalle?> = _pedidoId
        .flatMapLatest { id ->
            if (id != null) repository.observarPedidoPorId(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun cargarPedido(id: Int) {
        _pedidoId.value = id
    }

    fun cancelarPedido(onSuccess: () -> Unit) {
        val id = _pedidoId.value ?: return
        if (pedidoDetalle.value?.pedido?.estado != EstadoPedido.RECIBIDO.name) return
        viewModelScope.launch {
            repository.actualizarEstado(id, EstadoPedido.CANCELADO.name)
            onSuccess()
        }
    }
}