package cl.duoc.prestaaula.domain.validation

object EmailValidator {

    private val INSTITUTIONAL_REGEX = Regex("^[a-zA-Z0-9._%+-]+@marcelapaz\\.cl$", RegexOption.IGNORE_CASE)

    /**
     * Valida que el correo electrónico pertenezca al dominio institucional (@marcelapaz.cl)
     * y posea una estructura válida.
     */
    fun validate(email: String?): ValidationResult {
        if (email.isNullOrBlank()) {
            return ValidationResult.Error("El correo electrónico no puede estar vacío")
        }

        val trimmed = email.trim()

        if (!INSTITUTIONAL_REGEX.matches(trimmed)) {
            return ValidationResult.Error("El correo debe tener formato institucional (@marcelapaz.cl)")
        }

        return ValidationResult.Success
    }
}
