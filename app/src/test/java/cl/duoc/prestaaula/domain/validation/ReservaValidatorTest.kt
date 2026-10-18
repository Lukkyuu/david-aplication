package cl.duoc.prestaaula.domain.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ReservaValidatorTest {

    private val fechaHoy = LocalDate.of(2026, 4, 1)

    @Test
    fun `reserva con curso valido y fecha dentro del rango retorna Success`() {
        val result = ReservaValidator.validate(
            curso = "7° Básico A",
            fechaStr = "2026-04-05",
            fechaActual = fechaHoy
        )
        assertTrue(result.isValid)
    }

    @Test
    fun `reserva con curso vacio retorna Error`() {
        val result = ReservaValidator.validate(
            curso = "   ",
            fechaStr = "2026-04-05",
            fechaActual = fechaHoy
        )
        assertFalse(result.isValid)
    }

    @Test
    fun `reserva en fecha pasada retorna Error`() {
        val result = ReservaValidator.validate(
            curso = "8° Básico B",
            fechaStr = "2026-03-31",
            fechaActual = fechaHoy
        )
        assertFalse(result.isValid)
    }

    @Test
    fun `reserva con mas de 14 dias de anticipacion retorna Error`() {
        val result = ReservaValidator.validate(
            curso = "1° Medio A",
            fechaStr = "2026-04-20", // 19 días después
            fechaActual = fechaHoy
        )
        assertFalse(result.isValid)
    }

    @Test
    fun `reserva con formato de fecha invalido retorna Error`() {
        val result = ReservaValidator.validate(
            curso = "2° Medio B",
            fechaStr = "05/04/2026",
            fechaActual = fechaHoy
        )
        assertFalse(result.isValid)
    }
}
