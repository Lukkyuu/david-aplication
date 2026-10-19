package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.data.local.relation.PrestamoConDetalle
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.repository.EquipoRepository
import cl.duoc.prestaaula.domain.repository.PrestamoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class CraDashboardUiState(
    val disponiblesCount: Int = 0,
    val prestadosCount: Int = 0,
    val mantencionCount: Int = 0,
    val totalEquiposCount: Int = 0,
    val prestamosActivos: List<PrestamoConDetalle> = emptyList(),
    val isLoading: Boolean = false
)

class CraDashboardViewModel(
    private val equipoRepository: EquipoRepository,
    private val prestamoRepository: PrestamoRepository
) : ViewModel() {

    val uiState: StateFlow<CraDashboardUiState> = combine(
        equipoRepository.countPorEstadoFlow(EstadoEquipo.DISPONIBLE),
        equipoRepository.countPorEstadoFlow(EstadoEquipo.PRESTADO),
        equipoRepository.countPorEstadoFlow(EstadoEquipo.EN_MANTENCION),
        prestamoRepository.getPrestamosActivos()
    ) { disp, pres, mant, prestamos ->
        CraDashboardUiState(
            disponiblesCount = disp,
            prestadosCount = pres,
            mantencionCount = mant,
            totalEquiposCount = disp + pres + mant,
            prestamosActivos = prestamos,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CraDashboardUiState(isLoading = true)
    )
}
