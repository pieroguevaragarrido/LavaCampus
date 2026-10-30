package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.ClienteResumen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ClientesViewModel(
    repository: LavaRepository
) : ViewModel() {

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    val clientes: StateFlow<List<ClienteResumen>> = combine(
        repository.observarClientes(),
        _busqueda
    ) { lista, texto ->
        val consulta = texto.trim()
        if (consulta.isEmpty()) {
            lista
        } else {
            lista.filter {
                it.nombre.contains(consulta, ignoreCase = true) ||
                        it.codigo.contains(consulta, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun buscar(texto: String) {
        _busqueda.value = texto
    }
}