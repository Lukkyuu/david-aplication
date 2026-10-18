package cl.duoc.prestaaula.domain.validation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RutValidatorTest {

    @Test
    fun `ruts validos retornan Success`() {
        val rutsValidos = listOf(
            "14.567.890-2",
            "16.234.567-8",
            "13.987.654-K",
            "11.456.789-5",
            "12.345.678-5",
            "145678902",
            "13987654k",
            "14.567.890-0" // Válido por Módulo 11 (14567890 -> DV 0)
        )

        for (rut in rutsValidos) {
            val result = RutValidator.validate(rut)
            assertTrue("El RUT $rut debería ser válido", result.isValid)
        }
    }

    @Test
    fun `ruts invalidos o vacios retornan Error`() {
        val rutsInvalidos = listOf(
            "",
            "   ",
            "16.234.567-9", // DV incorrecto (debería ser 2 u 8)
            "12345",         // Demasiado corto
            "123456789012",  // Demasiado largo
            "ABCD.EFG-K"     // Letras en el cuerpo
        )

        for (rut in rutsInvalidos) {
            val result = RutValidator.validate(rut)
            assertFalse("El RUT $rut debería ser inválido", result.isValid)
        }
    }

    @Test
    fun `formatear rut de forma correcta`() {
        assertEquals("14.567.890-2", RutValidator.formatRut("145678902"))
        assertEquals("13.987.654-K", RutValidator.formatRut("13987654k"))
    }
}
