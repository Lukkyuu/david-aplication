package cl.duoc.prestaaula.domain.validation

object PasswordValidator {

    /**
     * Valida los requisitos de seguridad de las contraseñas:
     * - Mínimo 8 caracteres.
     * - Al menos una letra mayúscula.
     * - Al menos un número.
     * - Al menos un carácter especial (!@#$%^&*...).
     */
    fun validate(password: String?): ValidationResult {
        if (password.isNullOrBlank()) {
            return ValidationResult.Error("La contraseña no puede estar vacía")
        }

        if (password.length < 8) {
            return ValidationResult.Error("La contraseña debe tener al menos 8 caracteres")
        }

        if (!password.any { it.isUpperCase() }) {
            return ValidationResult.Error("La contraseña debe incluir al menos una letra mayúscula")
        }

        if (!password.any { it.isDigit() }) {
            return ValidationResult.Error("La contraseña debe incluir al menos un número")
        }

        val specialCharRegex = Regex("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]")
        if (!specialCharRegex.containsMatchIn(password)) {
            return ValidationResult.Error("La contraseña debe incluir al menos un carácter especial (ej: !@#$%)")
        }

        return ValidationResult.Success
    }
}
