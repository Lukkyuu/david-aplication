package cl.duoc.prestaaula.domain.usecase

import cl.duoc.prestaaula.data.local.entity.EquipoEntity
import cl.duoc.prestaaula.data.local.entity.PrestamoEntity
import cl.duoc.prestaaula.data.local.entity.ReservaEntity
import cl.duoc.prestaaula.data.local.relation.DocenteIncidenciaStats
import cl.duoc.prestaaula.data.local.relation.EquipoUsoStats
import cl.duoc.prestaaula.data.local.relation.PrestamoConDetalle
import cl.duoc.prestaaula.data.local.relation.ReservaConDetalle
import cl.duoc.prestaaula.domain.model.EstadoDevolucion
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.model.TipoEquipo
import cl.duoc.prestaaula.domain.repository.EquipoRepository
import cl.duoc.prestaaula.domain.repository.PrestamoRepository
import cl.duoc.prestaaula.domain.repository.ReservaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class UseCasesTest {

    private val fakeEquipoRepository = object : EquipoRepository {
        private val equipos = mutableMapOf(
            1L to EquipoEntity(id = 1L, tipo = TipoEquipo.NOTEBOOK, marca = "Lenovo", modelo = "E14", codigoInterno = "LIB-01", estado = EstadoEquipo.DISPONIBLE),
            2L to EquipoEntity(id = 2L, tipo = TipoEquipo.NOTEBOOK, marca = "HP", modelo = "ProBook", codigoInterno = "LIB-02", estado = EstadoEquipo.EN_MANTENCION)
        )

        override fun getEquipos(query: String, tipo: TipoEquipo?, estado: EstadoEquipo?): Flow<List<EquipoEntity>> =
            flowOf(equipos.values.toList())

        override fun getEquipoById(id: Long): Flow<EquipoEntity?> = flowOf(equipos[id])

        override suspend fun getEquipoByIdSync(id: Long): EquipoEntity? = equipos[id]

        override suspend fun getEquipoByCodigo(codigo: String): EquipoEntity? =
            equipos.values.firstOrNull { it.codigoInterno.equals(codigo, ignoreCase = true) }

        override fun getDisponiblesParaFechaYBloque(fecha: String, bloqueId: Long, tipo: TipoEquipo?): Flow<List<EquipoEntity>> =
            flowOf(equipos.values.filter { it.estado == EstadoEquipo.DISPONIBLE })

        override suspend fun insertEquipo(equipo: EquipoEntity): Long {
            equipos[equipo.id] = equipo
            return equipo.id
        }

        override suspend fun updateEquipo(equipo: EquipoEntity) {
            equipos[equipo.id] = equipo
        }

        override suspend fun deleteEquipo(equipo: EquipoEntity) {
            equipos.remove(equipo.id)
        }

        override suspend fun updateEstado(id: Long, estado: EstadoEquipo) {
            equipos[id]?.let { equipos[id] = it.copy(estado = estado) }
        }

        override fun countPorEstadoFlow(estado: EstadoEquipo): Flow<Int> =
            flowOf(equipos.values.count { it.estado == estado })
    }

    private val fakeReservaRepository = object : ReservaRepository {
        private var vencidasCount = 0

        override suspend fun crearReserva(reserva: ReservaEntity): Result<Long> {
            return Result.success(100L)
        }

        override suspend fun cancelarReserva(reservaId: Long): Result<Unit> = Result.success(Unit)

        override fun getReservasPorDocente(docenteId: Long): Flow<List<ReservaConDetalle>> = flowOf(emptyList())

        override fun getReservasActivasPorDocente(docenteId: Long): Flow<List<ReservaConDetalle>> = flowOf(emptyList())

        override fun getReservasHoyPorDocente(docenteId: Long, fechaHoy: String): Flow<List<ReservaConDetalle>> = flowOf(emptyList())

        override suspend fun verificarConflictoReserva(equipoId: Long, fecha: String, bloqueId: Long): Boolean = false

        override suspend fun countReservasActivasDocenteEnBloque(docenteId: Long, fecha: String, bloqueId: Long): Int = 0

        override suspend fun revisarYVencerReservas(fechaHoy: String, horaActual: String): Int {
            vencidasCount = 3
            return vencidasCount
        }
    }

    private val fakePrestamoRepository = object : PrestamoRepository {
        override suspend fun registrarPrestamo(prestamo: PrestamoEntity): Result<Long> = Result.success(50L)

        override suspend fun registrarDevolucion(
            prestamoId: Long,
            estadoAlDevolver: EstadoDevolucion,
            observaciones: String,
            rutaFotoEvidencia: String?,
            encargadoRecibioId: Long?
        ): Result<Unit> = Result.success(Unit)

        override fun getPrestamosActivos(): Flow<List<PrestamoConDetalle>> = flowOf(emptyList())
        override fun getPrestamosActivosPorDocente(docenteId: Long): Flow<List<PrestamoConDetalle>> = flowOf(emptyList())
        override fun getHistorialPorEquipo(equipoId: Long): Flow<List<PrestamoConDetalle>> = flowOf(emptyList())
        override suspend fun getUltimoPrestamoPorEquipo(equipoId: Long): PrestamoConDetalle? = null
        override fun getAllHistorial(): Flow<List<PrestamoConDetalle>> = flowOf(emptyList())
        override fun countPrestamosActivosFlow(): Flow<Int> = flowOf(0)
        override fun getEquiposMasPrestados(limit: Int): Flow<List<EquipoUsoStats>> = flowOf(emptyList())
        override fun getDocentesConMasIncidencias(limit: Int): Flow<List<DocenteIncidenciaStats>> = flowOf(emptyList())
    }

    @Test
    fun `ReservarEquipoUseCase exitoso`() = runBlocking {
        val useCase = ReservarEquipoUseCase(fakeReservaRepository, fakeEquipoRepository)
        val result = useCase(
            equipoId = 1L,
            docenteId = 10L,
            curso = "1° Medio C",
            fecha = "2026-04-05",
            bloqueId = 1L,
            fechaActual = LocalDate.of(2026, 4, 1)
        )
        assertTrue(result.isSuccess)
    }

    @Test
    fun `ReservarEquipoUseCase falla si equipo en mantencion`() = runBlocking {
        val useCase = ReservarEquipoUseCase(fakeReservaRepository, fakeEquipoRepository)
        val result = useCase(
            equipoId = 2L, // Equipo en mantención
            docenteId = 10L,
            curso = "1° Medio C",
            fecha = "2026-04-05",
            bloqueId = 1L,
            fechaActual = LocalDate.of(2026, 4, 1)
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `RegistrarDevolucionUseCase falla si esta danado y sin foto`() = runBlocking {
        val useCase = RegistrarDevolucionUseCase(fakePrestamoRepository)
        val result = useCase(
            prestamoId = 50L,
            estadoAlDevolver = EstadoDevolucion.DANADO,
            observaciones = "Pantalla rota",
            rutaFotoEvidencia = null
        )
        assertTrue(result.isFailure)
    }

    @Test
    fun `RegistrarDevolucionUseCase exitoso con foto si esta danado`() = runBlocking {
        val useCase = RegistrarDevolucionUseCase(fakePrestamoRepository)
        val result = useCase(
            prestamoId = 50L,
            estadoAlDevolver = EstadoDevolucion.DANADO,
            observaciones = "Pantalla rota por caída",
            rutaFotoEvidencia = "file:///evidencia.jpg"
        )
        assertTrue(result.isSuccess)
    }

    @Test
    fun `VencerReservasUseCase retorna cantidad de reservas vencidas`() = runBlocking {
        val useCase = VencerReservasUseCase(fakeReservaRepository)
        val count = useCase("2026-04-01", "12:00")
        assertEquals(3, count)
    }
}
