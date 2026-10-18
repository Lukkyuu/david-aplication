package cl.duoc.prestaaula.domain.usecase

import cl.duoc.prestaaula.data.local.entity.ReservaEntity
import cl.duoc.prestaaula.domain.model.EstadoReserva
import cl.duoc.prestaaula.domain.repository.EquipoRepository
import cl.duoc.prestaaula.domain.repository.ReservaRepository
import cl.duoc.prestaaula.domain.validation.EquipoValidator
import cl.duoc.prestaaula.domain.validation.ReservaValidator
import cl.duoc.prestaaula.domain.validation.ValidationResult
import java.time.LocalDate

class ReservarEquipoUseCase(
    private val reservaRepository: ReservaRepository,
    private val equipoRepository: EquipoRepository
) {
    suspend operator fun invoke(
        equipoId: Long,
        docenteId: Long,
        curso: String,
        fecha: String,
        bloqueId: Long,
        observaciones: String = "",
        fechaActual: LocalDate = LocalDate.now()
    ): Result<Long> {
        val validacionReserva = ReservaValidator.validate(curso, fecha, fechaActual)
        if (validacionReserva is ValidationResult.Error) {
            return Result.failure(IllegalArgumentException(validacionReserva.message))
        }

        val equipo = equipoRepository.getEquipoByIdSync(equipoId)
            ?: return Result.failure(IllegalArgumentException("El equipo especificado no existe."))

        val validacionEquipo = EquipoValidator.validateDisponibleParaReserva(equipo.estado)
        if (validacionEquipo is ValidationResult.Error) {
            return Result.failure(IllegalStateException(validacionEquipo.message))
        }

        val reservaEntity = ReservaEntity(
            equipoId = equipoId,
            docenteId = docenteId,
            curso = curso.trim(),
            fecha = fecha,
            bloqueId = bloqueId,
            estado = EstadoReserva.ACTIVA,
            observaciones = observaciones.trim()
        )

        return reservaRepository.crearReserva(reservaEntity)
    }
}
