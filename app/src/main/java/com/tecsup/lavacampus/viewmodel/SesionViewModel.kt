package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class SesionViewModel(
    private val repository: LavaRepository,
    private val preferenciasStore: PreferenciasStore
) : ViewModel() {

    val nombre: StateFlow<String?> = preferenciasStore.nombre
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val correo: StateFlow<String?> = preferenciasStore.correo
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val codigo: StateFlow<String?> = preferenciasStore.codigo
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val rol: StateFlow<String?> = preferenciasStore.rol
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val modoOscuro: StateFlow<Boolean> = preferenciasStore.modoOscuro
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val notificaciones: StateFlow<Boolean> = preferenciasStore.notificaciones
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val recibirPedidos: StateFlow<Boolean> = preferenciasStore.recibirPedidos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Contadores del Perfil del estudiante
    private val pedidosUsuario: Flow<List<PedidoConDetalle>> =
        preferenciasStore.usuarioId.flatMapLatest { id ->
            if (id != null) repository.observarPedidosDeUsuario(id) else flowOf(emptyList())
        }

    val pedidosActivos: StateFlow<Int> = pedidosUsuario
        .map { lista ->
            lista.count {
                it.pedido.estado == EstadoPedido.RECIBIDO.name ||
                        it.pedido.estado == EstadoPedido.EN_PROCESO.name ||
                        it.pedido.estado == EstadoPedido.LISTO.name
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val pedidosTotales: StateFlow<Int> = pedidosUsuario
        .map { lista -> lista.count { it.pedido.estado != EstadoPedido.CANCELADO.name } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val kilosLavados: StateFlow<Double> = pedidosUsuario
        .map { lista ->
            lista
                .filter { it.pedido.estado != EstadoPedido.CANCELADO.name }
                .sumOf { pedido ->
                    pedido.detalles
                        .filter { it.servicio.unidad == "KG" }
                        .sumOf { it.detalle.cantidad }
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun cambiarModoOscuro(activo: Boolean) {
        viewModelScope.launch { preferenciasStore.guardarModoOscuro(activo) }
    }

    fun cambiarNotificaciones(activo: Boolean) {
        viewModelScope.launch { preferenciasStore.guardarNotificaciones(activo) }
    }

    fun cambiarRecibirPedidos(activo: Boolean) {
        viewModelScope.launch { preferenciasStore.guardarRecibirPedidos(activo) }
    }

    /** Guarda el otro perfil (CLIENTE o ADMIN) y avisa para navegar a su grafo. */
    fun cambiarPerfil(destino: String, onListo: (String) -> Unit) {
        viewModelScope.launch {
            var usuario = if (destino == "CLIENTE") {
                val ultimo = preferenciasStore.ultimoCliente.first()
                if (ultimo != null) repository.obtenerUsuario(ultimo) else null
            } else {
                null
            }
            if (usuario == null || usuario.rol != destino) {
                usuario = repository.primerUsuarioDeRol(destino)
            }
            if (usuario != null) {
                preferenciasStore.guardarSesion(
                    id = usuario.id,
                    usuarioId = usuario.id,
                    nombre = usuario.nombre,
                    correo = usuario.correo,
                    rol = usuario.rol,
                    codigo = usuario.codigo
                )
                onListo(usuario.rol)
            }
        }
    }

    fun cerrarSesion(onSuccess: () -> Unit) {
        viewModelScope.launch {
            preferenciasStore.cerrarSesion()
            onSuccess()
        }
    }
}