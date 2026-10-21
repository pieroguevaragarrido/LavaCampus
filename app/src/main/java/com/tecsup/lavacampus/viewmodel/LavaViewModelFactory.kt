package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository

class LavaViewModelFactory(
    private val repository: LavaRepository,
    private val preferenciasStore: PreferenciasStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SesionViewModel::class.java) -> {
                SesionViewModel(repository, preferenciasStore) as T
            }
            modelClass.isAssignableFrom(InicioViewModel::class.java) -> {
                InicioViewModel(repository, preferenciasStore) as T
            }
            modelClass.isAssignableFrom(NuevoPedidoViewModel::class.java) -> {
                NuevoPedidoViewModel(repository, preferenciasStore) as T
            }
            else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}