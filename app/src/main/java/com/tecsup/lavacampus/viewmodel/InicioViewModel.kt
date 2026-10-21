package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class InicioViewModel(
    private val repository: LavaRepository,
    private val preferenciasStore: PreferenciasStore
) : ViewModel() {

    val nombre: StateFlow<String?> = preferenciasStore.nombre.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), "Ana García"
    )

    private val pedidosUsuario: Flow<List<PedidoConDetalle>> = preferenciasStore.usuarioId
        .flatMapLatest { id ->
            if (id != null) {
                repository.observarPedidosDeUsuario(id)
            } else {
                flowOf(emptyList())
            }
        }

    val pedidoActivo: StateFlow<PedidoConDetalle?> = pedidosUsuario
        .map { lista ->
            lista.firstOrNull { p ->
                val est = p.pedido.estado
                est == EstadoPedido.RECIBIDO.name ||
                        est == EstadoPedido.EN_PROCESO.name ||
                        est == EstadoPedido.LISTO.name
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val recientes: StateFlow<List<PedidoConDetalle>> = pedidosUsuario
        .map { lista ->
            lista.take(2)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}