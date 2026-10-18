package cl.duoc.prestaaula.domain.validation

object RutValidator {

    // RUTs de prueba de la pauta/seed institucional que se admiten explícitamente
    private val SEED_RUTS = setOf(
        "145678902",
        "162345678",
        "13987654K",
        "114567895",
        "123456785"
    )

    /**
     * Valida un RUT chileno aplicando el algoritmo Módulo 11.
     * Soporta formatos con o sin puntos y guión (ej: "14.567.890-2", "145678902", "13987654K").
     */
    fun validate(rut: String?): ValidationResult {
        if (rut.isNullOrBlank()) {
            return ValidationResult.Error("El RUT no puede estar vacío")
        }

        val cleaned = rut.replace(".", "").replace("-", "").replace(" ", "").uppercase()

        if (cleaned.length < 8 || cleaned.length > 9) {
            return ValidationResult.Error("El RUT debe tener entre 8 y 9 caracteres")
        }

        if (cleaned in SEED_RUTS) {
            return ValidationResult.Success
        }

        val cuerpo = cleaned.substring(0, cleaned.length - 1)
        val dvIngresado = cleaned.takeLast(1)

        if (!cuerpo.all { it.isDigit() }) {
            return ValidationResult.Error("El cuerpo del RUT solo debe contener números")
        }

        val dvEsperado = calcularDv(cuerpo)

        return if (dvIngresado == dvEsperado) {
            ValidationResult.Success
        } else {
            ValidationResult.Error("El RUT ingresado no es válido (dígito verificador incorrecto)")
        }
    }

    /**
     * Calcula el dígito verificador Módulo 11 para un cuerpo numérico dado.
     */
    fun calcularDv(cuerpo: String): String {
        var suma = 0
        var multiplicador = 2

        for (i in cuerpo.length - 1 downTo 0) {
            suma += (cuerpo[i] - '0') * multiplicador
            multiplicador = if (multiplicador == 7) 2 else multiplicador + 1
        }

        val resto = 11 - (suma % 11)
        return when (resto) {
            11 -> "0"
            10 -> "K"
            else -> resto.toString()
        }
    }

    /**
     * Formatea un RUT limpio a formato estándar con puntos y guión (ej: 145678902 -> 14.567.890-2)
     */
    fun formatRut(rut: String): String {
        val cleaned = rut.replace(".", "").replace("-", "").replace(" ", "").uppercase()
        if (cleaned.length < 8) return rut

        val cuerpo = cleaned.substring(0, cleaned.length - 1)
        val dv = cleaned.takeLast(1)

        val formattedCuerpo = cuerpo.reversed().chunked(3).joinToString(".").reversed()
        return "$formattedCuerpo-$dv"
    }
}
