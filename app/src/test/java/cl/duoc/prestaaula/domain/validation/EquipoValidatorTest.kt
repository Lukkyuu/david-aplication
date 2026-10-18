package cl.duoc.prestaaula.domain.validation

import cl.duoc.prestaaula.domain.model.EstadoEquipo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EquipoValidatorTest {

    @Test
    fun `equipo con campos obligatorios retorna Success`() {
        val result = EquipoValidator.validate(
            codigoInterno = "LIB-M01",
            marca = "Lenovo",
            modelo = "ThinkPad E14"
        )
        assertTrue(result.isValid)
    }

    @Test
    fun `equipo con codigo vacio o corto retorna Error`() {
        val r1 = EquipoValidator.validate(codigoInterno = "", marca = "Lenovo", modelo = "ThinkPad")
        assertFalse(r1.isValid)

        val r2 = EquipoValidator.validate(codigoInterno = "AB", marca = "Lenovo", modelo = "ThinkPad")
        assertFalse(r2.isValid)
    }

    @Test
    fun `verificacion de disponibilidad segun estado`() {
        assertTrue(EquipoValidator.validateDisponibleParaReserva(EstadoEquipo.DISPONIBLE).isValid)
        assertFalse(EquipoValidator.validateDisponibleParaReserva(EstadoEquipo.PRESTADO).isValid)
        assertFalse(EquipoValidator.validateDisponibleParaReserva(EstadoEquipo.EN_MANTENCION).isValid)
        assertFalse(EquipoValidator.validateDisponibleParaReserva(EstadoEquipo.DE_BAJA).isValid)
    }
}
