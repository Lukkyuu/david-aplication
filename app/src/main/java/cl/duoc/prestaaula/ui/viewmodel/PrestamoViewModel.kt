package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.data.local.entity.EquipoEntity
import cl.duoc.prestaaula.data.local.entity.UsuarioEntity
import cl.duoc.prestaaula.data.local.relation.ReservaConDetalle
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.repository.AuthRepository
import cl.duoc.prestaaula.domain.repository.EquipoRepository
import cl.duoc.prestaaula.domain.repository.ReservaRepository
import cl.duoc.prestaaula.domain.usecase.RegistrarPrestamoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PrestamoUiState(
    val codigoEquipo: String = "",
    val equipoSeleccionado: EquipoEntity? = null,
    val equiposDisponibles: List<EquipoEntity> = emptyList(),
    val docentes: List<UsuarioEntity> = emptyList(),
    val docenteSeleccionado: UsuarioEntity? = null,
    val docenteRut: String = "",
    val curso: String = "",
    val cursoError: String? = null,
    val observaciones: String = "",
    val reservasHoy: List<ReservaConDetalle> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val successMessage: String? = null
)

class PrestamoViewModel(
    codigoInicial: String?,
    private val equipoRepository: EquipoRepository,
    private val authRepository: AuthRepository,
    private val reservaRepository: ReservaRepository,
    private val registrarPrestamoUseCase: RegistrarPrestamoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        PrestamoUiState(codigoEquipo = codigoInicial ?: "")
    )
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    init {
        cargarEquiposDisponibles()
        cargarDocentes()
        cargarReservasHoy()

        if (!codigoInicial.isNullOrBlank()) {
            buscarEquipoPorCodigo(codigoInicial)
        }
    }

    private fun cargarEquiposDisponibles() {
        equipoRepository.getEquipos(estado = EstadoEquipo.DISPONIBLE)
            .onEach { lista ->
                _uiState.update { current ->
                    val seleccionado = current.equipoSeleccionado ?: lista.find { 
                        it.codigoInterno.equals(current.codigoEquipo, ignoreCase = true) 
                    }
                    current.copy(
                        equiposDisponibles = lista,
                        equipoSeleccionado = seleccionado
                    )
                }
            }
            .catch { }
            .launchIn(viewModelScope)
    }

    private fun cargarDocentes() {
        authRepository.getDocentes()
            .onEach { lista ->
                _uiState.update { current ->
                    val seleccionado = current.docenteSeleccionado ?: lista.find { 
                        it.rut.trim() == current.docenteRut.trim() 
                    }
                    current.copy(
                        docentes = lista,
                        docenteSeleccionado = seleccionado
                    )
                }
            }
            .catch { }
            .launchIn(viewModelScope)
    }

    private fun cargarReservasHoy() {
        val hoyStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        reservaRepository.getReservasActivasHoy(hoyStr)
            .onEach { lista ->
                _uiState.update { it.copy(reservasHoy = lista) }
            }
            .catch { }
            .launchIn(viewModelScope)
    }

    fun onCodigoChanged(codigo: String) {
        _uiState.update { current ->
            val match = current.equiposDisponibles.find { 
                it.codigoInterno.equals(codigo.trim(), ignoreCase = true) 
            }
            current.copy(
                codigoEquipo = codigo,
                equipoSeleccionado = match,
                errorMessage = null
            )
        }
    }

    fun onEquipoSelected(equipo: EquipoEntity) {
        _uiState.update {
            it.copy(
                equipoSeleccionado = equipo,
                codigoEquipo = equipo.codigoInterno,
                errorMessage = null
            )
        }
    }

    fun onDocenteSelected(docente: UsuarioEntity) {
        _uiState.update {
            it.copy(
                docenteSeleccionado = docente,
                docenteRut = docente.rut,
                errorMessage = null
            )
        }
    }

    fun onDocenteRutChanged(rut: String) {
        _uiState.update { current ->
            val match = current.docentes.find { 
                it.rut.replace(".", "").replace("-", "").equals(
                    rut.replace(".", "").replace("-", "").trim(),
                    ignoreCase = true
                )
            }
            current.copy(
                docenteRut = rut,
                docenteSeleccionado = match,
                errorMessage = null
            )
        }
    }

    fun onCursoChanged(curso: String) {
        _uiState.update {
            it.copy(
                curso = curso,
                cursoError = if (curso.isBlank()) "El curso es obligatorio" else null,
                errorMessage = null
            )
        }
    }

    fun onObservacionesChanged(obs: String) {
        _uiState.update { it.copy(observaciones = obs) }
    }

    private fun buscarEquipoPorCodigo(codigo: String) {
        viewModelScope.launch {
            val equipo = equipoRepository.getEquipoByCodigo(codigo)
            if (equipo != null) {
                _uiState.update { it.copy(equipoSeleccionado = equipo, codigoEquipo = equipo.codigoInterno) }
            }
        }
    }

    fun entregarReserva(reserva: ReservaConDetalle) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = registrarPrestamoUseCase(
                equipoId = reserva.equipo.id,
                docenteId = reserva.docente.id,
                curso = reserva.reserva.curso,
                reservaId = reserva.reserva.id,
                observaciones = "Préstamo correspondiente a reserva B${reserva.bloque.numeroBloque}"
            )
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = "¡Préstamo registrado exitosamente para ${reserva.docente.nombre} ${reserva.docente.apellido}!"
                    )
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Error al registrar entrega de reserva"
                    )
                }
            }
        }
    }

    fun confirmarPrestamo() {
        val state = _uiState.value
        val curso = state.curso.trim()
        if (curso.isBlank()) {
            _uiState.update { it.copy(cursoError = "Debe ingresar el curso (ej: 2° Medio A)") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 1. Resolver equipo
            val equipo = state.equipoSeleccionado ?: run {
                val buscado = equipoRepository.getEquipoByCodigo(state.codigoEquipo.trim())
                if (buscado == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No se encontró ningún equipo con código '${state.codigoEquipo}'."
                        )
                    }
                    return@launch
                }
                buscado
            }

            // 2. Resolver docente
            val docente = state.docenteSeleccionado ?: run {
                val buscado = authRepository.getUsuarioByRut(state.docenteRut.trim())
                if (buscado == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "No se encontró ningún docente con RUT '${state.docenteRut}'."
                        )
                    }
                    return@launch
                }
                buscado
            }

            val result = registrarPrestamoUseCase(
                equipoId = equipo.id,
                docenteId = docente.id,
                curso = curso,
                reservaId = null,
                observaciones = state.observaciones.trim()
            )

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = "¡Equipo ${equipo.codigoInterno} entregado correctamente a ${docente.nombre} ${docente.apellido}!"
                    )
                }
            }.onFailure { ex ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = ex.message ?: "Error al registrar préstamo"
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
