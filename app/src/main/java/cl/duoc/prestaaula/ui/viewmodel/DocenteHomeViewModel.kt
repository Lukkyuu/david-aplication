package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.data.local.relation.PrestamoConDetalle
import cl.duoc.prestaaula.data.local.relation.ReservaConDetalle
import cl.duoc.prestaaula.domain.repository.AuthRepository
import cl.duoc.prestaaula.domain.repository.PrestamoRepository
import cl.duoc.prestaaula.domain.repository.ReservaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

data class DocenteHomeUiState(
    val docenteNombre: String = "",
    val reservasHoy: List<ReservaConDetalle> = emptyList(),
    val prestamosActivos: List<PrestamoConDetalle> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class DocenteHomeViewModel(
    private val authRepository: AuthRepository,
    private val reservaRepository: ReservaRepository,
    private val prestamoRepository: PrestamoRepository
) : ViewModel() {

    private val hoyStr = LocalDate.now().toString()

    val uiState: StateFlow<DocenteHomeUiState> = authRepository.currentSession
        .flatMapLatest { session ->
            val userId = session.userId
            if (userId <= 0L) {
                flowOf(DocenteHomeUiState())
            } else {
                combine(
                    reservaRepository.getReservasHoyPorDocente(userId, hoyStr),
                    prestamoRepository.getPrestamosActivosPorDocente(userId)
                ) { reservas, prestamos ->
                    DocenteHomeUiState(
                        docenteNombre = session.nombre.ifBlank { "Docente" },
                        reservasHoy = reservas,
                        prestamosActivos = prestamos,
                        isLoading = false
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DocenteHomeUiState(isLoading = true)
        )
}
