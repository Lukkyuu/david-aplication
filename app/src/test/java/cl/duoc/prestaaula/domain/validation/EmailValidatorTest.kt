package cl.duoc.prestaaula.domain.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmailValidatorTest {

    @Test
    fun `correos institucionales validos retornan Success`() {
        val correosValidos = listOf(
            "carlos.gomez@marcelapaz.cl",
            "maria.rodriguez@marcelapaz.cl",
            "patricia.lagos@marcelapaz.cl",
            "ADMIN.FINANZAS@marcelapaz.cl"
        )

        for (email in correosValidos) {
            val result = EmailValidator.validate(email)
            assertTrue("El correo $email debería ser válido", result.isValid)
        }
    }

    @Test
    fun `correos invalidos o no institucionales retornan Error`() {
        val correosInvalidos = listOf(
            "",
            "   ",
            "carlos.gomez@gmail.com",
            "carlos.gomez@duoc.cl",
            "carlos.gomezmarcelapaz.cl",
            "carlos@marcelapaz.com"
        )

        for (email in correosInvalidos) {
            val result = EmailValidator.validate(email)
            assertFalse("El correo $email debería ser inválido", result.isValid)
        }
    }
}
