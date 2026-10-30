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
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(repository, preferenciasStore) as T
            }
            modelClass.isAssignableFrom(InicioViewModel::class.java) -> {
                InicioViewModel(repository, preferenciasStore) as T
            }
            modelClass.isAssignableFrom(MisPedidosViewModel::class.java) -> {
                MisPedidosViewModel(repository, preferenciasStore) as T
            }
            modelClass.isAssignableFrom(PanelViewModel::class.java) -> {
                PanelViewModel(repository) as T
            }
            modelClass.isAssignableFrom(PedidosViewModel::class.java) -> {
                PedidosViewModel(repository, preferenciasStore) as T
            }
            modelClass.isAssignableFrom(ServiciosViewModel::class.java) -> {
                ServiciosViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SesionViewModel::class.java) -> {
                SesionViewModel(repository, preferenciasStore) as T
            }
            else -> throw IllegalArgumentException("ViewModel no reconocido: ${modelClass.name}")
        }
    }
}