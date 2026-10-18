package cl.duoc.prestaaula.domain.usecase

import cl.duoc.prestaaula.domain.model.EstadoDevolucion
import cl.duoc.prestaaula.domain.repository.PrestamoRepository

class RegistrarDevolucionUseCase(
    private val prestamoRepository: PrestamoRepository
) {
    suspend operator fun invoke(
        prestamoId: Long,
        estadoAlDevolver: EstadoDevolucion,
        observaciones: String = "",
        rutaFotoEvidencia: String? = null,
        encargadoRecibioId: Long? = null
    ): Result<Unit> {
        val obsTrimmed = observaciones.trim()

        if ((estadoAlDevolver == EstadoDevolucion.DANADO ||
             estadoAlDevolver == EstadoDevolucion.INCOMPLETO ||
             estadoAlDevolver == EstadoDevolucion.CON_OBSERVACIONES) && obsTrimmed.isBlank()) {
            return Result.failure(IllegalArgumentException("Debe ingresar observaciones detalladas cuando el equipo no se devuelve en estado óptimo."))
        }

        if (estadoAlDevolver == EstadoDevolucion.DANADO && rutaFotoEvidencia.isNullOrBlank()) {
            return Result.failure(IllegalArgumentException("Es obligatorio adjuntar una fotografía como evidencia cuando el equipo presenta daños."))
        }

        return prestamoRepository.registrarDevolucion(
            prestamoId = prestamoId,
            estadoAlDevolver = estadoAlDevolver,
            observaciones = obsTrimmed,
            rutaFotoEvidencia = rutaFotoEvidencia,
            encargadoRecibioId = encargadoRecibioId
        )
    }
}
