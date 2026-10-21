package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.model.LineaPedido
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PedidosViewModel(
    private val repository: LavaRepository,
    private val preferenciasStore: PreferenciasStore
) : ViewModel() {

    val pedidosTodos: StateFlow<List<PedidoConDetalle>> = repository.observarPedidos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pedidosUsuario: StateFlow<List<PedidoConDetalle>> = preferenciasStore.usuarioId
        .flatMapLatest { id ->
            if (id != null) repository.observarPedidosDeUsuario(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val casillerosOcupados: StateFlow<List<String>> = repository.observarCasillerosOcupados()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun crearPedido(
        usuarioId: Int,
        lineas: List<LineaPedido>,
        fechaEntrega: String,
        nota: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            repository.crearPedido(usuarioId, lineas, fechaEntrega, nota)
            onSuccess()
        }
    }

    fun cambiarEstado(id: Int, estado: EstadoPedido, casillero: String?) {
        viewModelScope.launch {
            repository.cambiarEstado(id, estado, casillero)
        }
    }

    fun marcarPagado(id: Int, pagado: Boolean) {
        viewModelScope.launch {
            repository.marcarPagado(id, pagado)
        }
    }
}