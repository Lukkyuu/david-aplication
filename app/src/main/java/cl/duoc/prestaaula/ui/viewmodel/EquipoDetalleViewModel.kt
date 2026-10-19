package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.data.local.entity.EquipoEntity
import cl.duoc.prestaaula.data.local.relation.PrestamoConDetalle
import cl.duoc.prestaaula.domain.repository.EquipoRepository
import cl.duoc.prestaaula.domain.repository.PrestamoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class EquipoDetalleUiState(
    val equipo: EquipoEntity? = null,
    val historial: List<PrestamoConDetalle> = emptyList(),
    val ultimoResponsable: PrestamoConDetalle? = null,
    val isLoading: Boolean = false
)

class EquipoDetalleViewModel(
    private val equipoId: Long,
    private val equipoRepository: EquipoRepository,
    private val prestamoRepository: PrestamoRepository
) : ViewModel() {

    val uiState: StateFlow<EquipoDetalleUiState> = combine(
        equipoRepository.getEquipoById(equipoId),
        prestamoRepository.getHistorialPorEquipo(equipoId)
    ) { equipo, historialList ->
        EquipoDetalleUiState(
            equipo = equipo,
            historial = historialList,
            ultimoResponsable = historialList.firstOrNull(),
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = EquipoDetalleUiState(isLoading = true)
    )
}
