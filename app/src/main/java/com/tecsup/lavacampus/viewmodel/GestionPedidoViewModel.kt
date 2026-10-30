package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class GestionPedidoViewModel(
    private val repository: LavaRepository
) : ViewModel() {

    private val _pedidoId = MutableStateFlow<Int?>(null)

    val pedido: StateFlow<PedidoConDetalle?> = _pedidoId
        .flatMapLatest { id ->
            if (id != null) repository.observarPedidoPorId(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val casillerosOcupados: StateFlow<List<String>> = repository.observarCasillerosOcupados()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _casilleroElegido = MutableStateFlow<String?>(null)
    val casilleroElegido: StateFlow<String?> = _casilleroElegido.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun cargar(id: Int) {
        _pedidoId.value = id
    }

    fun elegirCasillero(casillero: String) {
        _casilleroElegido.value = casillero
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    fun marcarPagado(pagado: Boolean) {
        val id = _pedidoId.value ?: return
        viewModelScope.launch { repository.marcarPagado(id, pagado) }
    }

    /** Avanza SOLO al estado siguiente, validando casillero (LISTO) y cobro (ENTREGADO). */
    fun avanzar() {
        val actual = pedido.value?.pedido ?: return
        val siguiente = EstadoPedido.valueOf(actual.estado).siguiente()
        if (siguiente == null) {
            _mensaje.value = "Este pedido ya no puede avanzar"
            return
        }

        when (siguiente) {
            EstadoPedido.LISTO -> {
                val casillero = _casilleroElegido.value
                if (casillero == null) {
                    _mensaje.value = "Elige un casillero antes de marcarlo como Listo"
                    return
                }
                if (casillero in casillerosOcupados.value && casillero != actual.casillero) {
                    _mensaje.value = "El casillero $casillero ya está ocupado"
                    return
                }
                viewModelScope.launch {
                    repository.cambiarEstado(actual.id, EstadoPedido.LISTO, casillero)
                }
            }

            EstadoPedido.ENTREGADO -> {
                if (!actual.pagado) {
                    _mensaje.value = "Primero marca el pedido como Cobrado"
                    return
                }
                viewModelScope.launch {
                    repository.cambiarEstado(actual.id, EstadoPedido.ENTREGADO, null)
                }
            }

            else -> {
                viewModelScope.launch {
                    repository.cambiarEstado(actual.id, siguiente, null)
                }
            }
        }
    }

    /** El administrador solo cancela pedidos RECIBIDO o EN_PROCESO. */
    fun cancelar(onListo: () -> Unit) {
        val actual = pedido.value?.pedido ?: return
        val puede = actual.estado == EstadoPedido.RECIBIDO.name ||
                actual.estado == EstadoPedido.EN_PROCESO.name
        if (!puede) {
            _mensaje.value = "Solo se cancela en Recibido o En proceso"
            return
        }
        viewModelScope.launch {
            repository.cambiarEstado(actual.id, EstadoPedido.CANCELADO, null)
            onListo()
        }
    }
}