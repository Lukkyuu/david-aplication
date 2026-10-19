package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.data.local.relation.PrestamoConDetalle
import cl.duoc.prestaaula.data.local.relation.ReservaConDetalle
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.repository.EquipoRepository
import cl.duoc.prestaaula.domain.repository.PrestamoRepository
import cl.duoc.prestaaula.domain.repository.ReservaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CraDashboardUiState(
    val disponiblesCount: Int = 0,
    val prestadosCount: Int = 0,
    val mantencionCount: Int = 0,
    val deBajaCount: Int = 0,
    val totalEquiposCount: Int = 0,
    val prestamosActivos: List<PrestamoConDetalle> = emptyList(),
    val reservasHoy: List<ReservaConDetalle> = emptyList(),
    val isLoading: Boolean = false
)

class CraDashboardViewModel(
    private val equipoRepository: EquipoRepository,
    private val prestamoRepository: PrestamoRepository,
    private val reservaRepository: ReservaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CraDashboardUiState(isLoading = true))
    val uiState: StateFlow<CraDashboardUiState> = _uiState.asStateFlow()

    init {
        val dispFlow = equipoRepository.countPorEstadoFlow(EstadoEquipo.DISPONIBLE)
        val prestFlow = equipoRepository.countPorEstadoFlow(EstadoEquipo.PRESTADO)
        val mantFlow = equipoRepository.countPorEstadoFlow(EstadoEquipo.EN_MANTENCION)
        val bajaFlow = equipoRepository.countPorEstadoFlow(EstadoEquipo.DE_BAJA)

        combine(dispFlow, prestFlow, mantFlow, bajaFlow) { disp, prest, mant, baja ->
            _uiState.update {
                it.copy(
                    disponiblesCount = disp,
                    prestadosCount = prest,
                    mantencionCount = mant,
                    deBajaCount = baja,
                    totalEquiposCount = disp + prest + mant + baja,
                    isLoading = false
                )
            }
        }.catch { }.launchIn(viewModelScope)

        prestamoRepository.getPrestamosActivos()
            .onEach { lista ->
                _uiState.update { it.copy(prestamosActivos = lista) }
            }
            .catch { }
            .launchIn(viewModelScope)

        val hoyStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        reservaRepository.getReservasActivasHoy(hoyStr)
            .onEach { lista ->
                _uiState.update { it.copy(reservasHoy = lista) }
            }
            .catch { }
            .launchIn(viewModelScope)
    }
}
