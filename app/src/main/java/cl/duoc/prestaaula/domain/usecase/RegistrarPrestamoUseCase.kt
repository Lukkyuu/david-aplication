package cl.duoc.prestaaula.domain.usecase

import cl.duoc.prestaaula.data.local.entity.PrestamoEntity
import cl.duoc.prestaaula.domain.repository.PrestamoRepository

class RegistrarPrestamoUseCase(
    private val prestamoRepository: PrestamoRepository
) {
    suspend operator fun invoke(
        equipoId: Long,
        docenteId: Long,
        curso: String,
        reservaId: Long? = null,
        observaciones: String = ""
    ): Result<Long> {
        if (curso.trim().isBlank()) {
            return Result.failure(IllegalArgumentException("El curso es obligatorio para registrar el préstamo."))
        }

        val prestamo = PrestamoEntity(
            equipoId = equipoId,
            docenteId = docenteId,
            reservaId = reservaId,
            curso = curso.trim(),
            fechaHoraRetiro = System.currentTimeMillis(),
            observaciones = observaciones.trim()
        )

        return prestamoRepository.registrarPrestamo(prestamo)
    }
}
