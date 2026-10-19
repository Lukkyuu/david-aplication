package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.data.local.entity.BloqueHorarioEntity
import cl.duoc.prestaaula.data.local.entity.EquipoEntity
import cl.duoc.prestaaula.domain.model.TipoEquipo
import cl.duoc.prestaaula.domain.repository.BloqueHorarioRepository
import cl.duoc.prestaaula.domain.repository.EquipoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate

data class DisponibilidadUiState(
    val fechaSeleccionada: String = LocalDate.now().toString(),
    val bloqueSeleccionadoId: Long = 1L,
    val tipoFiltro: TipoEquipo? = null,
    val bloques: List<BloqueHorarioEntity> = emptyList(),
    val equiposDisponibles: List<EquipoEntity> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class DisponibilidadViewModel(
    private val equipoRepository: EquipoRepository,
    private val bloqueHorarioRepository: BloqueHorarioRepository
) : ViewModel() {

    private val _filterState = MutableStateFlow(DisponibilidadUiState())

    val uiState: StateFlow<DisponibilidadUiState> = combine(
        _filterState,
        bloqueHorarioRepository.getBloques()
    ) { state, bloquesList ->
        state.copy(bloques = bloquesList)
    }.flatMapLatest { state ->
        equipoRepository.getDisponiblesParaFechaYBloque(state.fechaSeleccionada, state.bloqueSeleccionadoId, state.tipoFiltro)
            .map { equipos ->
                state.copy(equiposDisponibles = equipos, isLoading = false)
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DisponibilidadUiState(isLoading = true)
    )

    fun onFechaChanged(fecha: String) {
        _filterState.update { it.copy(fechaSeleccionada = fecha) }
    }

    fun onBloqueChanged(bloqueId: Long) {
        _filterState.update { it.copy(bloqueSeleccionadoId = bloqueId) }
    }

    fun onTipoFiltroChanged(tipo: TipoEquipo?) {
        _filterState.update { it.copy(tipoFiltro = tipo) }
    }
}
