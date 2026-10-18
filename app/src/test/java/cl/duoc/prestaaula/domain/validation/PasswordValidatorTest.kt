package cl.duoc.prestaaula.domain.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordValidatorTest {

    @Test
    fun `contrasenas validas retornan Success`() {
        val contrasenasValidas = listOf(
            "Docente2026!",
            "Cra2026!Paz",
            "Utp2026!Paz",
            "Finanzas2026!",
            "P@ssw0rd2026"
        )

        for (pass in contrasenasValidas) {
            val result = PasswordValidator.validate(pass)
            assertTrue("La contraseña $pass debería ser válida", result.isValid)
        }
    }

    @Test
    fun `contrasenas invalidas retornan Error`() {
        val contrasenasInvalidas = listOf(
            "",
            "   ",
            "Doc20!",      // Menos de 8 caracteres
            "docente2026!", // Sin mayúscula
            "DocentePaz!",  // Sin número
            "Docente2026"   // Sin carácter especial
        )

        for (pass in contrasenasInvalidas) {
            val result = PasswordValidator.validate(pass)
            assertFalse("La contraseña $pass debería ser inválida", result.isValid)
        }
    }
}
