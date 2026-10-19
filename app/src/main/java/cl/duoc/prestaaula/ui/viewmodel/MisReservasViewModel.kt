package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.data.local.relation.ReservaConDetalle
import cl.duoc.prestaaula.domain.repository.AuthRepository
import cl.duoc.prestaaula.domain.repository.ReservaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MisReservasUiState(
    val reservas: List<ReservaConDetalle> = emptyList(),
    val filtroEstado: String = "TODAS", // TODAS, ACTIVA, RETIRADA, CANCELADA, VENCIDA
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class MisReservasViewModel(
    private val authRepository: AuthRepository,
    private val reservaRepository: ReservaRepository
) : ViewModel() {

    private val _filtro = MutableStateFlow("TODAS")

    val uiState: StateFlow<MisReservasUiState> = combine(
        authRepository.currentSession.flatMapLatest { session ->
            val userId = session.userId
            if (userId <= 0L) {
                flowOf(emptyList())
            } else {
                reservaRepository.getReservasPorDocente(userId)
            }
        },
        _filtro
    ) { reservas, filtro ->
        val filtradas = if (filtro == "TODAS") {
            reservas
        } else {
            reservas.filter { it.reserva.estado.name == filtro }
        }
        MisReservasUiState(reservas = filtradas, filtroEstado = filtro, isLoading = false)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MisReservasUiState(isLoading = true)
    )

    fun setFiltro(filtro: String) {
        _filtro.value = filtro
    }

    fun cancelarReserva(reservaId: Long) {
        viewModelScope.launch {
            reservaRepository.cancelarReserva(reservaId)
        }
    }
}
