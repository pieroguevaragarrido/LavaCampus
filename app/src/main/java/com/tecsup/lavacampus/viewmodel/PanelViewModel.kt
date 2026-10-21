package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.ClienteResumen
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.model.IngresoServicio
import kotlinx.coroutines.flow.*
import java.time.LocalDate

class PanelViewModel(
    private val repository: LavaRepository
) : ViewModel() {

    private val hoy = LocalDate.now().toString()
    private val hace7Dias = LocalDate.now().minusDays(7).toString()

    val clientes: StateFlow<List<ClienteResumen>> = repository.observarClientes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pedidosHoy: StateFlow<Int> = repository.observarPedidosDeFecha(hoy)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val enProceso: StateFlow<Int> = repository.observarCantidadPorEstado(EstadoPedido.EN_PROCESO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val ingresosSemana: StateFlow<Double> = repository.observarIngresosDesde(hace7Dias)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val ingresosPorServicio: StateFlow<List<IngresoServicio>> = repository.observarIngresosPorServicio(hace7Dias)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}