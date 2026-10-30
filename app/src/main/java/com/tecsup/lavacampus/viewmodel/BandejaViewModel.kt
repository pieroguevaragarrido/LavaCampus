package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class FiltroBandeja(val etiqueta: String, val estado: EstadoPedido?) {
    TODOS("Todos", null),
    RECIBIDO("Recibido", EstadoPedido.RECIBIDO),
    EN_PROCESO("En proceso", EstadoPedido.EN_PROCESO),
    LISTO("Listo", EstadoPedido.LISTO)
}

class BandejaViewModel(
    private val repository: LavaRepository
) : ViewModel() {

    private val _filtro = MutableStateFlow(FiltroBandeja.TODOS)
    val filtro: StateFlow<FiltroBandeja> = _filtro.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    val pedidos: StateFlow<List<PedidoConDetalle>> = combine(
        repository.observarTodosPedidos(),
        _filtro,
        _busqueda
    ) { lista, filtro, texto ->
        val consulta = texto.trim().removePrefix("#")
        lista.filter { item ->
            val coincideEstado = filtro.estado == null || item.pedido.estado == filtro.estado?.name
            val coincideTexto = consulta.isEmpty() ||
                    item.usuario.nombre.contains(consulta, ignoreCase = true) ||
                    item.pedido.id.toString().contains(consulta)
            coincideEstado && coincideTexto
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun cambiarFiltro(filtro: FiltroBandeja) {
        _filtro.value = filtro
    }

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    /** RECIBIDO -> EN_PROCESO (no necesita casillero ni cobro). */
    fun iniciar(item: PedidoConDetalle) {
        if (item.pedido.estado != EstadoPedido.RECIBIDO.name) return
        viewModelScope.launch {
            repository.cambiarEstado(item.pedido.id, EstadoPedido.EN_PROCESO, null)
        }
    }
}