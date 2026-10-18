package cl.duoc.prestaaula.domain.validation

import cl.duoc.prestaaula.domain.model.EstadoEquipo

object EquipoValidator {

    /**
     * Valida los campos obligatorios para el registro o edición de un equipo en inventario.
     */
    fun validate(
        codigoInterno: String?,
        marca: String?,
        modelo: String?
    ): ValidationResult {
        if (codigoInterno.isNullOrBlank()) {
            return ValidationResult.Error("El código interno del equipo es obligatorio")
        }

        if (codigoInterno.trim().length < 3) {
            return ValidationResult.Error("El código interno debe tener al menos 3 caracteres")
        }

        if (marca.isNullOrBlank()) {
            return ValidationResult.Error("La marca del equipo es obligatoria")
        }

        if (modelo.isNullOrBlank()) {
            return ValidationResult.Error("El modelo del equipo es obligatorio")
        }

        return ValidationResult.Success
    }

    /**
     * Verifica si el estado del equipo permite realizar un préstamo o reserva.
     */
    fun validateDisponibleParaReserva(estado: EstadoEquipo): ValidationResult {
        return when (estado) {
            EstadoEquipo.DISPONIBLE -> ValidationResult.Success
            EstadoEquipo.PRESTADO -> ValidationResult.Error("El equipo ya se encuentra prestado actualmente")
            EstadoEquipo.EN_MANTENCION -> ValidationResult.Error("El equipo se encuentra en mantención y no está disponible")
            EstadoEquipo.DE_BAJA -> ValidationResult.Error("El equipo está dado de baja")
        }
    }
}
