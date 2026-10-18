package cl.duoc.prestaaula.domain.usecase

import cl.duoc.prestaaula.domain.repository.ReservaRepository

class VencerReservasUseCase(
    private val reservaRepository: ReservaRepository
) {
    suspend operator fun invoke(fechaHoy: String, horaActual: String): Int {
        return reservaRepository.revisarYVencerReservas(fechaHoy, horaActual)
    }
}
