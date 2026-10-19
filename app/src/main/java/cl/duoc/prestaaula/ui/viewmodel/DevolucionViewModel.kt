package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.data.local.relation.PrestamoConDetalle
import cl.duoc.prestaaula.domain.model.EstadoDevolucion
import cl.duoc.prestaaula.domain.repository.AuthRepository
import cl.duoc.prestaaula.domain.repository.PrestamoRepository
import cl.duoc.prestaaula.domain.usecase.RegistrarDevolucionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    val isSuccess: Boolean = false,
    val successMessage: String? = null
)

class DevolucionViewModel(
    codigoInicial: String?,
    private val prestamoRepository: PrestamoRepository,
    private val authRepository: AuthRepository,
    private val registrarDevolucionUseCase: RegistrarDevolucionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DevolucionUiState(codigoInput = codigoInicial ?: "")
    )
    val uiState: StateFlow<DevolucionUiState> = _uiState.asStateFlow()

    init {
        cargarPrestamosActivos(codigoInicial)
    }

    private fun cargarPrestamosActivos(codigoInicial: String?) {
        prestamoRepository.getPrestamosActivos()
            .onEach { lista ->
                _uiState.update { current ->
                    val seleccionado = current.prestamoSeleccionado ?: lista.find {
                        it.equipo.codigoInterno.equals(codigoInicial ?: current.codigoInput, ignoreCase = true)
                    }
                    current.copy(
                        prestamosActivos = lista,
                        prestamoSeleccionado = seleccionado
                    )
                }
            }
            .catch { }
            .launchIn(viewModelScope)
    }

    fun seleccionarPrestamo(prestamo: PrestamoConDetalle) {
        _uiState.update {
            it.copy(
                prestamoSeleccionado = prestamo,
                codigoInput = prestamo.equipo.codigoInterno,
                errorMessage = null
            )
        }
    }

    fun onCodigoChanged(codigo: String) {
        _uiState.update { current ->
            val match = current.prestamosActivos.find {
                it.equipo.codigoInterno.equals(codigo.trim(), ignoreCase = true)
            }
            current.copy(
                codigoInput = codigo,
                prestamoSeleccionado = match,
                errorMessage = null
            )
        }
    }

    fun onEstadoChanged(estado: EstadoDevolucion) {
        _uiState.update {
            it.copy(
                estadoDevolucion = estado,
                observacionesError = null,
                fotoError = null,
                errorMessage = null
            )
        }
    }

    fun onObservacionesChanged(obs: String) {
        _uiState.update {
            it.copy(
                observaciones = obs,
                observacionesError = null,
                errorMessage = null
            )
        }
    }

    fun onFotoCaptured(uri: String) {
        _uiState.update {
            it.copy(
                fotoUri = uri,
                fotoError = null,
                errorMessage = null
            )
        }
    }

    fun confirmarDevolucion() {
        val state = _uiState.value
        val prestamo = state.prestamoSeleccionado

        if (prestamo == null) {
            _uiState.update { it.copy(errorMessage = "Debe seleccionar el equipo o préstamo activo a recepcionar.") }
            return
        }

        val obs = state.observaciones.trim()
        val necesitaObs = state.estadoDevolucion != EstadoDevolucion.BUENO
        if (necesitaObs && obs.isBlank()) {
            _uiState.update { it.copy(observacionesError = "Debe ingresar observaciones detalladas sobre el estado del equipo.") }
            return
        }

        if (state.estadoDevolucion == EstadoDevolucion.DANADO && state.fotoUri.isNullOrBlank()) {
            _uiState.update { it.copy(fotoError = "Es obligatorio adjuntar una fotografía de evidencia por equipo dañado.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val session = authRepository.currentSession.firstOrNull()
            val encargadoId = session?.userId

            val result = registrarDevolucionUseCase(
                prestamoId = prestamo.prestamo.id,
                estadoAlDevolver = state.estadoDevolucion,
                observaciones = obs,
                rutaFotoEvidencia = state.fotoUri,
                encargadoRecibioId = encargadoId
            )

            result.onSuccess {
                val mensajeEstado = when (state.estadoDevolucion) {
                    EstadoDevolucion.BUENO -> "Equipo devuelto en buen estado y disponible en inventario."
                    EstadoDevolucion.CON_OBSERVACIONES -> "Equipo devuelto con observaciones registradas."
                    EstadoDevolucion.DANADO -> "Equipo devuelto DAÑADO. Pasó automáticamente a EN MANTENCIÓN con evidencia fotográfica."
                    EstadoDevolucion.INCOMPLETO -> "Equipo devuelto INCOMPLETO. Pasó a EN MANTENCIÓN."
                }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = "¡Devolución de ${prestamo.equipo.codigoInterno} registrada con éxito! $mensajeEstado",
                        prestamoSeleccionado = null,
                        observaciones = "",
                        fotoUri = null
                    )
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Error al registrar devolución"
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
