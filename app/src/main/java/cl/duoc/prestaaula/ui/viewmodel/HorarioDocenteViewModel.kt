package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.data.local.entity.BloqueHorarioEntity
import cl.duoc.prestaaula.data.local.entity.DisponibilidadDocenteEntity
import cl.duoc.prestaaula.domain.model.DiaSemana
import cl.duoc.prestaaula.domain.repository.AuthRepository
import cl.duoc.prestaaula.domain.repository.BloqueHorarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HorarioDocenteUiState(
    val docenteId: Long = 0L,
    val diaSeleccionado: DiaSemana = DiaSemana.LUNES,
    val bloques: List<BloqueHorarioEntity> = emptyList(),
    val bloquesDocentePorDia: Map<DiaSemana, Set<Long>> = emptyMap(),
    val isSaving: Boolean = false,
    val mensaje: String? = null
)

class HorarioDocenteViewModel(
    private val authRepository: AuthRepository,
    private val bloqueHorarioRepository: BloqueHorarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HorarioDocenteUiState())
    val uiState: StateFlow<HorarioDocenteUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        bloqueHorarioRepository.getBloques()
            .onEach { lista ->
                _uiState.update { it.copy(bloques = lista) }
            }
            .catch { }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            val session = authRepository.currentSession.firstOrNull()
            val docenteId = session?.userId ?: 1L
            _uiState.update { it.copy(docenteId = docenteId) }

            bloqueHorarioRepository.getHorarioDocente(docenteId)
                .onEach { disps ->
                    val mapa = mutableMapOf<DiaSemana, MutableSet<Long>>()
                    DiaSemana.values().forEach { d -> mapa[d] = mutableSetOf() }

                    disps.forEach { item ->
                        mapa[item.disponibilidad.diaSemana]?.add(item.bloque.id)
                    }

                    _uiState.update { it.copy(bloquesDocentePorDia = mapa) }
                }
                .catch { }
                .launchIn(viewModelScope)
        }
    }

    fun seleccionarDia(dia: DiaSemana) {
        _uiState.update { it.copy(diaSeleccionado = dia, mensaje = null) }
    }

    fun toggleBloque(dia: DiaSemana, bloqueId: Long) {
        _uiState.update { current ->
            val mapa = current.bloquesDocentePorDia.toMutableMap()
            val set = mapa[dia]?.toMutableSet() ?: mutableSetOf()
            if (set.contains(bloqueId)) {
                set.remove(bloqueId)
            } else {
                set.add(bloqueId)
            }
            mapa[dia] = set
            current.copy(bloquesDocentePorDia = mapa, mensaje = null)
        }
    }

    fun guardarHorario() {
        val state = _uiState.value
        val docenteId = state.docenteId
        if (docenteId <= 0L) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val listaNuevos = mutableListOf<DisponibilidadDocenteEntity>()
            state.bloquesDocentePorDia.forEach { (dia, bloquesSet) ->
                bloquesSet.forEach { bloqueId ->
                    listaNuevos.add(
                        DisponibilidadDocenteEntity(
                            id = 0L,
                            docenteId = docenteId,
                            diaSemana = dia,
                            bloqueId = bloqueId
                        )
                    )
                }
            }

            bloqueHorarioRepository.guardarHorarioDocente(docenteId, listaNuevos)

            _uiState.update {
                it.copy(
                    isSaving = false,
                    mensaje = "¡Horario semanal actualizado correctamente!"
                )
            }
        }
    }

    fun clearMensaje() {
        _uiState.update { it.copy(mensaje = null) }
    }
}
