package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MisPedidosViewModel(
    private val repository: LavaRepository,
    private val preferenciasStore: PreferenciasStore
) : ViewModel() {

    val serviciosActivos = repository.observarServiciosActivos().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val pedidosUsuario = preferenciasStore.usuarioId
        .flatMapLatest { id ->
            if (id != null) repository.observarPedidosDeUsuario(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activos: StateFlow<List<PedidoConDetalle>> = pedidosUsuario
        .map { lista ->
            lista.filter {
                it.pedido.estado == EstadoPedido.RECIBIDO.name ||
                        it.pedido.estado == EstadoPedido.EN_PROCESO.name ||
                        it.pedido.estado == EstadoPedido.LISTO.name
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val historial: StateFlow<List<PedidoConDetalle>> = pedidosUsuario
        .map { lista ->
            lista.filter {
                it.pedido.estado == EstadoPedido.ENTREGADO.name ||
                        it.pedido.estado == EstadoPedido.CANCELADO.name
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cancelarPedido(pedidoId: Int) {
        viewModelScope.launch {
            repository.cambiarEstado(pedidoId, EstadoPedido.CANCELADO, null)
        }
    }
}