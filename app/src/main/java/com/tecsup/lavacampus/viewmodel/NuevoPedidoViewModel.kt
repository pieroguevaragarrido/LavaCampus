package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.LineaPedido
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

class NuevoPedidoViewModel(
    private val repository: LavaRepository,
    private val preferenciasStore: PreferenciasStore
) : ViewModel() {

    val serviciosActivos: StateFlow<List<Servicio>> = repository.observarServiciosActivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _cantidades = MutableStateFlow<Map<Int, Double>>(emptyMap())
    val cantidades: StateFlow<Map<Int, Double>> = _cantidades.asStateFlow()

    private val _nota = MutableStateFlow("")
    val nota: StateFlow<String> = _nota.asStateFlow()

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    // Estado reactivo del Total
    val total: StateFlow<Double> = combine(serviciosActivos, _cantidades) { servicios, cants ->
        cants.entries.sumOf { (servicioId, cantidad) ->
            val s = servicios.find { it.id == servicioId }
            (s?.precio ?: 0.0) * cantidad
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Estado reactivo de la Fecha de Entrega (salta domingos)
    val fechaEntrega: StateFlow<String> = combine(serviciosActivos, _cantidades) { servicios, cants ->
        val seleccionados = cants.filter { it.value > 0 }.keys
        if (seleccionados.isEmpty()) {
            LocalDate.now().toString()
        } else {
            val maxHoras = servicios
                .filter { it.id in seleccionados }
                .maxOfOrNull { it.horasEntrega } ?: 24

            val dias = (maxHoras / 24).toLong()
            var fecha = LocalDate.now().plusDays(dias)

            if (fecha.dayOfWeek == DayOfWeek.SUNDAY) {
                fecha = fecha.plusDays(1)
            }
            fecha.toString()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocalDate.now().toString())

    fun cambiarNota(nuevaNota: String) {
        _nota.value = nuevaNota
    }

    fun cambiarCantidad(servicio: Servicio, delta: Double) {
        val mapaActual = _cantidades.value.toMutableMap()
        val actual = mapaActual[servicio.id] ?: 0.0

        val nuevaCantidad = if (servicio.unidad == "KG") {
            if (actual == 0.0 && delta > 0) {
                1.0
            } else {
                (actual + delta).coerceIn(0.0, 10.0)
            }
        } else {
            (actual + delta).coerceIn(0.0, 30.0)
        }

        if (servicio.unidad == "KG" && nuevaCantidad < 1.0) {
            mapaActual.remove(servicio.id)
        } else if (nuevaCantidad <= 0.0) {
            mapaActual.remove(servicio.id)
        } else {
            mapaActual[servicio.id] = nuevaCantidad
        }

        _cantidades.value = mapaActual
    }

    fun confirmarPedido(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _mensajeError.value = null
            val usuarioId = preferenciasStore.usuarioId.first() ?: return@launch

            val activos = repository.contarActivos(usuarioId)
            if (activos >= 3) {
                _mensajeError.value = "Ya tienes 3 pedidos activos"
                return@launch
            }

            val servicios = serviciosActivos.value
            val lineas = _cantidades.value.mapNotNull { (servicioId, cantidad) ->
                val s = servicios.find { it.id == servicioId }
                if (s != null && cantidad > 0) {
                    LineaPedido(
                        servicioId = servicioId,
                        nombreServicio = s.nombre,
                        cantidad = cantidad,
                        precioUnitario = s.precio
                    )
                } else null
            }

            if (lineas.isEmpty()) return@launch

            repository.crearPedido(usuarioId, lineas, fechaEntrega.value, _nota.value)
            onSuccess()
        }
    }
}