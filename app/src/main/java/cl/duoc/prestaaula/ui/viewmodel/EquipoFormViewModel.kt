package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.data.local.entity.EquipoEntity
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.model.TipoEquipo
import cl.duoc.prestaaula.domain.repository.EquipoRepository
import cl.duoc.prestaaula.domain.validation.EquipoValidator
import cl.duoc.prestaaula.domain.validation.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EquipoFormUiState(
    val equipoId: Long? = null,
    val tipo: TipoEquipo = TipoEquipo.NOTEBOOK,
    val marca: String = "",
    val modelo: String = "",
    val codigoInterno: String = "",
    val estado: EstadoEquipo = EstadoEquipo.DISPONIBLE,
    val observaciones: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class EquipoFormViewModel(
    private val equipoIdArg: Long?,
    private val equipoRepository: EquipoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EquipoFormUiState(equipoId = equipoIdArg))
    val uiState: StateFlow<EquipoFormUiState> = _uiState.asStateFlow()

    init {
        if (equipoIdArg != null && equipoIdArg > 0L) {
            viewModelScope.launch {
                val eq = equipoRepository.getEquipoByIdSync(equipoIdArg)
                eq?.let {
                    _uiState.update {
                        it.copy(
                            tipo = eq.tipo,
                            marca = eq.marca,
                            modelo = eq.modelo,
                            codigoInterno = eq.codigoInterno,
                            estado = eq.estado,
                            observaciones = eq.observaciones
                        )
                    }
                }
            }
        }
    }

    fun onTipoChanged(tipo: TipoEquipo) {
        _uiState.update { it.copy(tipo = tipo) }
    }

    fun onMarcaChanged(marca: String) {
        _uiState.update { it.copy(marca = marca, errorMessage = null) }
    }

    fun onModeloChanged(modelo: String) {
        _uiState.update { it.copy(modelo = modelo, errorMessage = null) }
    }

    fun onCodigoChanged(codigo: String) {
        _uiState.update { it.copy(codigoInterno = codigo, errorMessage = null) }
    }

    fun onEstadoChanged(estado: EstadoEquipo) {
        _uiState.update { it.copy(estado = estado) }
    }

    fun onObservacionesChanged(obs: String) {
        _uiState.update { it.copy(observaciones = obs) }
    }

    fun guardarEquipo() {
        val state = _uiState.value
        val valResult = EquipoValidator.validate(state.codigoInterno, state.marca, state.modelo)
        if (valResult is ValidationResult.Error) {
            _uiState.update { it.copy(errorMessage = valResult.message) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val equipoEntity = EquipoEntity(
                id = state.equipoId ?: 0L,
                tipo = state.tipo,
                marca = state.marca.trim(),
                modelo = state.modelo.trim(),
                codigoInterno = state.codigoInterno.trim().uppercase(),
                estado = state.estado,
                observaciones = state.observaciones.trim()
            )

            try {
                if (state.equipoId == null || state.equipoId == 0L) {
                    equipoRepository.insertEquipo(equipoEntity)
                } else {
                    equipoRepository.updateEquipo(equipoEntity)
                }
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error al guardar equipo") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
