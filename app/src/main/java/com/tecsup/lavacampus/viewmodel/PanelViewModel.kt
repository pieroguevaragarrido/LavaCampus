package com.tecsup.lavacampus.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.components.graficos.DatoGrafico
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class PanelViewModel(
    private val repository: LavaRepository
) : ViewModel() {

    private val hoy: String = LocalDate.now().toString()

    // Lunes de esta semana (si hoy es lunes, es hoy; si es domingo, el lunes anterior)
    private val inicioSemana: String = LocalDate.now()
        .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        .toString()

    private val paleta = listOf(
        Color(0xFF6D28D9),
        Color(0xFF0891B2),
        Color(0xFFD97706),
        Color(0xFF16A34A),
        Color(0xFFDB2777)
    )

    val pedidosHoy: StateFlow<Int> = repository.observarPedidosDeFecha(hoy)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val enProceso: StateFlow<Int> = repository.observarCantidadPorEstado(EstadoPedido.EN_PROCESO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val porRecoger: StateFlow<Int> = repository.observarCantidadPorEstado(EstadoPedido.LISTO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val atrasados: StateFlow<Int> = repository.observarIdsAtrasados(hoy)
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val ingresosSemana: StateFlow<Double> = repository.observarIngresosDesde(inicioSemana)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val porEstado: StateFlow<List<DatoGrafico>> = combine(
        repository.observarCantidadPorEstado(EstadoPedido.RECIBIDO),
        repository.observarCantidadPorEstado(EstadoPedido.EN_PROCESO),
        repository.observarCantidadPorEstado(EstadoPedido.LISTO),
        repository.observarCantidadPorEstado(EstadoPedido.ENTREGADO)
    ) { recibido, proceso, listo, entregado ->
        listOf(
            DatoGrafico(EstadoPedido.RECIBIDO.etiqueta, recibido.toFloat(), EstadoPedido.RECIBIDO.color),
            DatoGrafico(EstadoPedido.EN_PROCESO.etiqueta, proceso.toFloat(), EstadoPedido.EN_PROCESO.color),
            DatoGrafico(EstadoPedido.LISTO.etiqueta, listo.toFloat(), EstadoPedido.LISTO.color),
            DatoGrafico(EstadoPedido.ENTREGADO.etiqueta, entregado.toFloat(), EstadoPedido.ENTREGADO.color)
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val porServicio: StateFlow<List<DatoGrafico>> = repository.observarIngresosPorServicio(inicioSemana)
        .map { lista ->
            lista.mapIndexed { i, ingreso ->
                DatoGrafico(ingreso.nombre, ingreso.total.toFloat(), paleta[i % paleta.size])
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}