package cl.duoc.prestaaula.domain.validation

/**
 * Representa el resultado de una validación de dominio.
 */
sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val message: String) : ValidationResult()

    val isValid: Boolean get() = this is Success

    fun errorMessageOrNull(): String? = (this as? Error)?.message
}
