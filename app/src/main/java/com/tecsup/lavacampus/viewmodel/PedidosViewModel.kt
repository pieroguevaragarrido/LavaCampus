package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PedidosViewModel(
    private val repository: LavaRepository,
    private val preferenciasStore: PreferenciasStore? = null
) : ViewModel() {

    constructor(repository: LavaRepository) : this(repository, null)

    val pedidos: StateFlow<List<PedidoConDetalle>> = repository.observarTodosPedidos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val casillerosOcupados: StateFlow<List<String>> = repository.observarCasillerosOcupados()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cambiarEstado(pedidoId: Int, nuevoEstado: Any, casillero: String? = null) {
        viewModelScope.launch {
            repository.cambiarEstado(pedidoId, nuevoEstado, casillero)
        }
    }

    fun marcarPagado(pedidoId: Int, pagado: Boolean = true) {
        viewModelScope.launch {
            repository.marcarPagado(pedidoId, pagado)
        }
    }
}