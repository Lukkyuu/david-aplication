package cl.duoc.prestaaula.domain.validation

import java.time.LocalDate
import java.time.format.DateTimeParseException

object ReservaValidator {

    /**
     * Valida los datos básicos de una solicitud de reserva:
     * - Curso / Asignatura no nulo ni vacío.
     * - Fecha con formato válido (YYYY-MM-DD).
     * - Fecha no sea en el pasado.
     * - Máximo 14 días de anticipación.
     */
    fun validate(
        curso: String?,
        fechaStr: String?,
        fechaActual: LocalDate = LocalDate.now()
    ): ValidationResult {
        if (curso.isNullOrBlank()) {
            return ValidationResult.Error("Debe especificar el curso o asignatura")
        }

        if (fechaStr.isNullOrBlank()) {
            return ValidationResult.Error("Debe seleccionar una fecha para la reserva")
        }

        val fechaReserva = try {
            LocalDate.parse(fechaStr)
        } catch (e: DateTimeParseException) {
            return ValidationResult.Error("Formato de fecha inválido. Utilice el formato YYYY-MM-DD")
        }

        if (fechaReserva.isBefore(fechaActual)) {
            return ValidationResult.Error("No se pueden realizar reservas para fechas pasadas")
        }

        val limiteMaximo = fechaActual.plusDays(14)
        if (fechaReserva.isAfter(limiteMaximo)) {
            return ValidationResult.Error("No se pueden realizar reservas con más de 14 días de anticipación")
        }

        return ValidationResult.Success
    }
}
