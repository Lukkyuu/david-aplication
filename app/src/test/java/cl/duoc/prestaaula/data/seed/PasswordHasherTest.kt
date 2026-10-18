package cl.duoc.prestaaula.data.seed

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {

    @Test
    fun hashPassword_generatesNonEmptyString() {
        val hash = PasswordHasher.hash("Docente2026!")
        assertTrue(hash.isNotBlank())
        assertEquals(64, hash.length) // SHA-256 produce 64 caracteres hex
    }

    @Test
    fun hashPassword_sameInput_producesSameHash() {
        val hash1 = PasswordHasher.hash("Docente2026!")
        val hash2 = PasswordHasher.hash("Docente2026!")
        assertEquals(hash1, hash2)
    }

    @Test
    fun hashPassword_differentInput_producesDifferentHash() {
        val hash1 = PasswordHasher.hash("Docente2026!")
        val hash2 = PasswordHasher.hash("Docente2026?")
        assertNotEquals(hash1, hash2)
    }

    @Test
    fun verifyPassword_correctPassword_returnsTrue() {
        val raw = "Cra2026!Paz"
        val hash = PasswordHasher.hash(raw)
        assertTrue(PasswordHasher.verify(raw, hash))
    }

    @Test
    fun verifyPassword_wrongPassword_returnsFalse() {
        val raw = "Cra2026!Paz"
        val hash = PasswordHasher.hash(raw)
        assertFalse(PasswordHasher.verify("WrongPassword", hash))
    }
}
