package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.data.local.entity.EquipoEntity
import cl.duoc.prestaaula.data.local.entity.UsuarioEntity
import cl.duoc.prestaaula.data.local.relation.ReservaConDetalle
import cl.duoc.prestaaula.domain.model.RolUsuario
import cl.duoc.prestaaula.domain.repository.AuthRepository
import cl.duoc.prestaaula.domain.repository.EquipoRepository
import cl.duoc.prestaaula.domain.repository.ReservaRepository
import cl.duoc.prestaaula.domain.usecase.RegistrarPrestamoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class PrestamoUiState(
    val reservasHoy: List<ReservaConDetalle> = emptyList(),
    val codigoEquipo: String = "",
    val equiposDisponibles: List<EquipoEntity> = emptyList(),
    val equipoSeleccionado: EquipoEntity? = null,
    val docenteRut: String = "",
    val docentes: List<UsuarioEntity> = emptyList(),
    val docenteSeleccionado: UsuarioEntity? = null,
    val curso: String = "",
    val cursoError: String? = null,
    val observaciones: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class PrestamoViewModel(
    private val codigoInicial: String?,
    private val equipoRepository: EquipoRepository,
    private val reservaRepository: ReservaRepository,
    private val authRepository: AuthRepository,
    private val registrarPrestamoUseCase: RegistrarPrestamoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrestamoUiState(codigoEquipo = codigoInicial.orEmpty()))
    val uiState: StateFlow<PrestamoUiState> = _uiState.asStateFlow()

    init {
        loadData()
        if (!codigoInicial.isNullOrBlank()) {
            buscarEquipoPorCodigo(codigoInicial)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            val hoyStr = LocalDate.now().toString()
            // Podemos obtener equipos disponibles y docentes
            val equiposList = equipoRepository.getDisponiblesParaFechaYBloque(hoyStr, 1L).first()
            // Nota: Para obtener docentes, podemos consultar vía usuarioDao si fuera necesario, o simular lista de docentes del seed
            _uiState.update {
                it.copy(
                    equiposDisponibles = equiposList,
                    docentes = emptyList() // Se puede rellenar o buscar por RUT
                )
            }
        }
    }

    fun onCodigoChanged(codigo: String) {
        _uiState.update { it.copy(codigoEquipo = codigo) }
        if (codigo.trim().length >= 3) {
            buscarEquipoPorCodigo(codigo)
        }
    }

    private fun buscarEquipoPorCodigo(codigo: String) {
        viewModelScope.launch {
            val eq = equipoRepository.getEquipoByCodigo(codigo.trim())
            _uiState.update { it.copy(equipoSeleccionado = eq) }
        }
    }

    fun onEquipoSelected(eq: EquipoEntity) {
        _uiState.update { it.copy(equipoSeleccionado = eq, codigoEquipo = eq.codigoInterno) }
    }

    fun onDocenteRutChanged(rut: String) {
        _uiState.update { it.copy(docenteRut = rut) }
    }

    fun onDocenteSelected(doc: UsuarioEntity) {
        _uiState.update { it.copy(docenteSeleccionado = doc, docenteRut = doc.rut) }
    }

    fun onCursoChanged(curso: String) {
        _uiState.update { it.copy(curso = curso, cursoError = null) }
    }

    fun onObservacionesChanged(obs: String) {
        _uiState.update { it.copy(observaciones = obs) }
    }

    fun entregarReserva(r: ReservaConDetalle) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            registrarPrestamoUseCase(
                equipoId = r.equipo.id,
                docenteId = r.docente.id,
                curso = r.reserva.curso,
                reservaId = r.reserva.id,
                observaciones = "Entregado desde reserva #${r.reserva.id}"
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "¡Préstamo entregado exitosamente para la reserva de ${r.docente.nombre}!"
                    )
                }
            }.onFailure { exc ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = exc.message ?: "Error al entregar reserva")
                }
            }
        }
    }

    fun confirmarPrestamo() {
        val state = _uiState.value
        val equipo = state.equipoSeleccionado
        if (equipo == null) {
            _uiState.update { it.copy(errorMessage = "Debe ingresar o seleccionar un equipo válido") }
            return
        }
        if (state.curso.trim().isBlank()) {
            _uiState.update { it.copy(cursoError = "El curso es obligatorio") }
            return
        }

        val docenteId = state.docenteSeleccionado?.id ?: 1L // Default al primer docente o buscar

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            registrarPrestamoUseCase(
                equipoId = equipo.id,
                docenteId = docenteId,
                curso = state.curso.trim(),
                observaciones = state.observaciones.trim()
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "¡Préstamo registrado exitosamente! Equipo en estado PRESTADO.",
                        codigoEquipo = "",
                        equipoSeleccionado = null,
                        curso = "",
                        observaciones = "",
                        docenteRut = ""
                    )
                }
            }.onFailure { exc ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = exc.message ?: "Error al registrar préstamo")
                }
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }
}
