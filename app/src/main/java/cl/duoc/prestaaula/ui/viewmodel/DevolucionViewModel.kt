package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.data.local.relation.PrestamoConDetalle
import cl.duoc.prestaaula.domain.model.EstadoDevolucion
import cl.duoc.prestaaula.domain.repository.AuthRepository
import cl.duoc.prestaaula.domain.repository.EquipoRepository
import cl.duoc.prestaaula.domain.repository.PrestamoRepository
import cl.duoc.prestaaula.domain.usecase.RegistrarDevolucionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DevolucionUiState(
    val prestamosActivos: List<PrestamoConDetalle> = emptyList(),
    val prestamoSeleccionado: PrestamoConDetalle? = null,
    val codigoInput: String = "",
    val estadoDevolucion: EstadoDevolucion = EstadoDevolucion.BUENO,
    val observaciones: String = "",
    val observacionesError: String? = null,
    val fotoUri: String? = null,
    val fotoError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class DevolucionViewModel(
    private val codigoInicial: String?,
    private val prestamoRepository: PrestamoRepository,
    private val equipoRepository: EquipoRepository,
    private val authRepository: AuthRepository,
    private val registrarDevolucionUseCase: RegistrarDevolucionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DevolucionUiState(codigoInput = codigoInicial.orEmpty()))
    val uiState: StateFlow<DevolucionUiState> = _uiState.asStateFlow()

    init {
        loadPrestamosActivos()
    }

    private fun loadPrestamosActivos() {
        viewModelScope.launch {
            val lista = prestamoRepository.getPrestamosActivos().first()
            _uiState.update { state ->
                val seleccionado = if (!codigoInicial.isNullOrBlank()) {
                    lista.firstOrNull { it.equipo.codigoInterno.equals(codigoInicial, ignoreCase = true) }
                } else null
                state.copy(
                    prestamosActivos = lista,
                    prestamoSeleccionado = seleccionado ?: state.prestamoSeleccionado
                )
            }
        }
    }

    fun seleccionarPrestamo(p: PrestamoConDetalle) {
        _uiState.update { it.copy(prestamoSeleccionado = p, codigoInput = p.equipo.codigoInterno) }
    }

    fun onCodigoChanged(codigo: String) {
        _uiState.update { state ->
            val match = state.prestamosActivos.firstOrNull { it.equipo.codigoInterno.equals(codigo.trim(), ignoreCase = true) }
            state.copy(
                codigoInput = codigo,
                prestamoSeleccionado = match ?: state.prestamoSeleccionado
            )
        }
    }

    fun onEstadoChanged(estado: EstadoDevolucion) {
        _uiState.update { it.copy(estadoDevolucion = estado, fotoError = null, observacionesError = null) }
    }

    fun onObservacionesChanged(obs: String) {
        _uiState.update { it.copy(observaciones = obs, observacionesError = null) }
    }

    fun onFotoCaptured(uriStr: String) {
        _uiState.update { it.copy(fotoUri = uriStr, fotoError = null) }
    }

    fun confirmarDevolucion() {
        val state = _uiState.value
        val prestamo = state.prestamoSeleccionado
        if (prestamo == null) {
            _uiState.update { it.copy(errorMessage = "Seleccione un préstamo activo o ingrese el código del equipo") }
            return
        }

        val requiereObs = state.estadoDevolucion == EstadoDevolucion.DANADO ||
                state.estadoDevolucion == EstadoDevolucion.INCOMPLETO ||
                state.estadoDevolucion == EstadoDevolucion.CON_OBSERVACIONES

        val obsError = if (requiereObs && state.observaciones.trim().isBlank()) {
            "Las observaciones son obligatorias para este estado"
        } else null

        val fotoError = if (state.estadoDevolucion == EstadoDevolucion.DANADO && state.fotoUri.isNullOrBlank()) {
            "Debe adjuntar una fotografía de evidencia del daño"
        } else null

        if (obsError != null || fotoError != null) {
            _uiState.update { it.copy(observacionesError = obsError, fotoError = fotoError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val session = authRepository.currentSession.first()
            val encargadoId = if (session.userId > 0L) session.userId else 1L

            registrarDevolucionUseCase(
                prestamoId = prestamo.prestamo.id,
                estadoAlDevolver = state.estadoDevolucion,
                observaciones = state.observaciones,
                rutaFotoEvidencia = state.fotoUri,
                encargadoRecibioId = encargadoId
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "¡Devolución registrada con éxito! Inventario actualizado.",
                        prestamoSeleccionado = null,
                        observaciones = "",
                        fotoUri = null
                    )
                }
                loadPrestamosActivos()
            }.onFailure { exc ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exc.message ?: "Error al registrar devolución"
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
