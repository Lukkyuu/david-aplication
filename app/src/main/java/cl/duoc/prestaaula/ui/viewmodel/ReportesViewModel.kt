package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.data.local.relation.DocenteIncidenciaStats
import cl.duoc.prestaaula.data.local.relation.EquipoUsoStats
import cl.duoc.prestaaula.data.local.relation.PrestamoConDetalle
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.repository.EquipoRepository
import cl.duoc.prestaaula.domain.repository.PrestamoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

data class ReportesUiState(
    val totalEquipos: Int = 0,
    val disponiblesCount: Int = 0,
    val prestadosCount: Int = 0,
    val mantencionCount: Int = 0,
    val deBajaCount: Int = 0,
    val equiposMasPrestados: List<EquipoUsoStats> = emptyList(),
    val docentesConIncidencias: List<DocenteIncidenciaStats> = emptyList(),
    val prestamosRecientes: List<PrestamoConDetalle> = emptyList(),
    val isLoading: Boolean = false
)

class ReportesViewModel(
    private val equipoRepository: EquipoRepository,
    private val prestamoRepository: PrestamoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportesUiState(isLoading = true))
    val uiState: StateFlow<ReportesUiState> = _uiState.asStateFlow()

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
                    totalEquipos = disp + prest + mant + baja,
                    isLoading = false
                )
            }
        }.catch { }.launchIn(viewModelScope)

        prestamoRepository.getEquiposMasPrestados(limit = 6)
            .onEach { lista ->
                _uiState.update { it.copy(equiposMasPrestados = lista) }
            }
            .catch { }
            .launchIn(viewModelScope)

        prestamoRepository.getDocentesConMasIncidencias(limit = 5)
            .onEach { lista ->
                _uiState.update { it.copy(docentesConIncidencias = lista) }
            }
            .catch { }
            .launchIn(viewModelScope)

        prestamoRepository.getAllHistorial()
            .onEach { lista ->
                _uiState.update { it.copy(prestamosRecientes = lista.take(10)) }
            }
            .catch { }
            .launchIn(viewModelScope)
    }
}
