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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PanelViewModel(private val repo: LavaRepository) : ViewModel() {

    private val hoyStr: String
    private val inicioSemanaStr: String

    init {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()

        // Fecha actual
        hoyStr = sdf.format(cal.time)

        // Obtener el lunes de la semana actual
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        inicioSemanaStr = sdf.format(cal.time)
    }

    // 1. Pedidos del día de hoy
    val pedidosHoy: StateFlow<Int> = repo.observarPedidosDeFecha(hoyStr)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // 2. Pedidos actualmente en proceso
    val enProceso: StateFlow<Int> = repo.observarCantidadPorEstado(EstadoPedido.EN_PROCESO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // 3. Pedidos listos por recoger
    val porRecoger: StateFlow<Int> = repo.observarCantidadPorEstado(EstadoPedido.LISTO)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // 4. Ingresos de la semana actual
    val ingresosSemana: StateFlow<Double> = repo.observarIngresosDesde(inicioSemanaStr)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

    // 5. Gráfico de Dona: Distribución de pedidos por estado
    val porEstado: StateFlow<List<DatoGrafico>> = combine(
        repo.observarCantidadPorEstado(EstadoPedido.RECIBIDO),
        repo.observarCantidadPorEstado(EstadoPedido.EN_PROCESO),
        repo.observarCantidadPorEstado(EstadoPedido.LISTO),
        repo.observarCantidadPorEstado(EstadoPedido.ENTREGADO)
    ) { recibido, enProceso, listo, entregado ->
        listOf(
            DatoGrafico("Recibido", recibido.toFloat(), Color(0xFF0891B2)),
            DatoGrafico("En Proceso", enProceso.toFloat(), Color(0xFF2563EB)),
            DatoGrafico("Listo", listo.toFloat(), Color(0xFF16A34A)),
            DatoGrafico("Entregado", entregado.toFloat(), Color(0xFF6B7280))
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 6. Gráfico de Barras: Ingresos por servicio
    private val coloresServicio = listOf(
        Color(0xFF0891B2), // Turquesa
        Color(0xFF6D28D9), // Violeta
        Color(0xFFD97706), // Ámbar
        Color(0xFF16A34A)  // Verde
    )

    val porServicio: StateFlow<List<DatoGrafico>> = repo.observarIngresosPorServicio(inicioSemanaStr)
        .map { listaIngresos ->
            listaIngresos.mapIndexed { index, ingreso ->
                DatoGrafico(
                    etiqueta = ingreso.nombre,
                    valor = ingreso.total.toFloat(),
                    color = coloresServicio[index % coloresServicio.size]
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}