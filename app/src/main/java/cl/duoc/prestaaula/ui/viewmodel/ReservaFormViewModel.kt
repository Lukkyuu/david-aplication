package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.data.local.entity.BloqueHorarioEntity
import cl.duoc.prestaaula.data.local.entity.EquipoEntity
import cl.duoc.prestaaula.domain.repository.AuthRepository
import cl.duoc.prestaaula.domain.repository.BloqueHorarioRepository
import cl.duoc.prestaaula.domain.repository.EquipoRepository
import cl.duoc.prestaaula.domain.usecase.ReservarEquipoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ReservaFormUiState(
    val equipoId: Long = 0L,
    val equipo: EquipoEntity? = null,
    val fecha: String = LocalDate.now().toString(),
    val bloqueId: Long = 1L,
    val curso: String = "",
    val observaciones: String = "",
    val bloques: List<BloqueHorarioEntity> = emptyList(),
    val equiposDisponibles: List<EquipoEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class ReservaFormViewModel(
    private val equipoIdArg: Long,
    private val authRepository: AuthRepository,
    private val equipoRepository: EquipoRepository,
    private val bloqueHorarioRepository: BloqueHorarioRepository,
    private val reservarEquipoUseCase: ReservarEquipoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservaFormUiState(equipoId = equipoIdArg))
    val uiState: StateFlow<ReservaFormUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val bloquesList = bloqueHorarioRepository.getBloques().first()
            val equipoSeleccionado = if (equipoIdArg > 0L) equipoRepository.getEquipoByIdSync(equipoIdArg) else null
            val equiposList = equipoRepository.getDisponiblesParaFechaYBloque(_uiState.value.fecha, _uiState.value.bloqueId).first()

            _uiState.update {
                it.copy(
                    bloques = bloquesList,
                    equipo = equipoSeleccionado,
                    equiposDisponibles = equiposList,
                    bloqueId = bloquesList.firstOrNull()?.id ?: 1L
                )
            }
        }
    }

    fun onEquipoChanged(id: Long) {
        viewModelScope.launch {
            val eq = equipoRepository.getEquipoByIdSync(id)
            _uiState.update { it.copy(equipoId = id, equipo = eq) }
        }
    }

    fun onFechaChanged(fecha: String) {
        _uiState.update { it.copy(fecha = fecha) }
        actualizarEquiposDisponibles()
    }

    fun onBloqueChanged(bloqueId: Long) {
        _uiState.update { it.copy(bloqueId = bloqueId) }
        actualizarEquiposDisponibles()
    }

    fun onCursoChanged(curso: String) {
        _uiState.update { it.copy(curso = curso, errorMessage = null) }
    }

    fun onObservacionesChanged(obs: String) {
        _uiState.update { it.copy(observaciones = obs) }
    }

    private fun actualizarEquiposDisponibles() {
        viewModelScope.launch {
            val state = _uiState.value
            val list = equipoRepository.getDisponiblesParaFechaYBloque(state.fecha, state.bloqueId).first()
            _uiState.update { it.copy(equiposDisponibles = list) }
        }
    }

    fun confirmarReserva() {
        val state = _uiState.value
        if (state.equipoId <= 0L && state.equipo == null) {
            _uiState.update { it.copy(errorMessage = "Debe seleccionar un equipo para reservar") }
            return
        }
        if (state.curso.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Debe ingresar el curso o asignatura") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val session = authRepository.currentSession.first()
            val docenteId = session.userId ?: 1L
            val targetEquipoId = state.equipoId.takeIf { it > 0L } ?: state.equipo?.id ?: 1L

            reservarEquipoUseCase(
                equipoId = targetEquipoId,
                docenteId = docenteId,
                curso = state.curso,
                fecha = state.fecha,
                bloqueId = state.bloqueId,
                observaciones = state.observaciones
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { exc ->
                _uiState.update { it.copy(isLoading = false, errorMessage = exc.message ?: "Error al realizar reserva") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
