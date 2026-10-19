package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.data.local.entity.EquipoEntity
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.model.TipoEquipo
import cl.duoc.prestaaula.domain.repository.EquipoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class InventarioUiState(
    val query: String = "",
    val tipoFiltro: TipoEquipo? = null,
    val estadoFiltro: EstadoEquipo? = null,
    val equipos: List<EquipoEntity> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class InventarioViewModel(
    private val equipoRepository: EquipoRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _tipoFiltro = MutableStateFlow<TipoEquipo?>(null)
    private val _estadoFiltro = MutableStateFlow<EstadoEquipo?>(null)

    val uiState: StateFlow<InventarioUiState> = _searchQuery
        .flatMapLatest { query ->
            kotlinx.coroutines.flow.combine(_tipoFiltro, _estadoFiltro) { tipo, estado ->
                Triple(query, tipo, estado)
            }
        }.flatMapLatest { (query, tipo, estado) ->
            equipoRepository.getEquipos(query, tipo, estado)
        }.let { flowOfEquipos ->
            kotlinx.coroutines.flow.combine(
                _searchQuery,
                _tipoFiltro,
                _estadoFiltro,
                flowOfEquipos
            ) { q, t, e, lista ->
                InventarioUiState(
                    query = q,
                    tipoFiltro = t,
                    estadoFiltro = e,
                    equipos = lista,
                    isLoading = false
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = InventarioUiState(isLoading = true)
        )

    fun onQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onTipoChanged(tipo: TipoEquipo?) {
        _tipoFiltro.value = tipo
    }

    fun onEstadoChanged(estado: EstadoEquipo?) {
        _estadoFiltro.value = estado
    }
}
