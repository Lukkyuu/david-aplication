package cl.duoc.prestaaula.data.repository

import cl.duoc.prestaaula.data.local.dao.EquipoDao
import cl.duoc.prestaaula.data.local.dao.PrestamoDao
import cl.duoc.prestaaula.data.local.dao.ReservaDao
import cl.duoc.prestaaula.data.local.entity.EquipoEntity
import cl.duoc.prestaaula.data.local.entity.PrestamoEntity
import cl.duoc.prestaaula.data.local.entity.ReservaEntity
import cl.duoc.prestaaula.data.local.transaction.DirectTransactionRunner
import cl.duoc.prestaaula.domain.model.EstadoDevolucion
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.model.EstadoReserva
import cl.duoc.prestaaula.domain.model.TipoEquipo
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Pruebas unitarias de las reglas de negocio en los repositorios de persistencia.
 */
class RepositoryRulesTest {

    private val equiposMemoria = mutableListOf<EquipoEntity>()
    private val reservasMemoria = mutableListOf<ReservaEntity>()
    private val prestamosMemoria = mutableListOf<PrestamoEntity>()

    // Fake DAOs
    private val fakeEquipoDao = object : EquipoDao {
        override suspend fun insert(equipo: EquipoEntity): Long {
            val id = (equiposMemoria.size + 1).toLong()
            val nuevo = equipo.copy(id = id)
            equiposMemoria.add(nuevo)
            return id
        }
        override suspend fun insertAll(equipos: List<EquipoEntity>): List<Long> {
            return equipos.map { insert(it) }
        }
        override suspend fun update(equipo: EquipoEntity) {
            val idx = equiposMemoria.indexOfFirst { it.id == equipo.id }
            if (idx >= 0) equiposMemoria[idx] = equipo
        }
        override suspend fun delete(equipo: EquipoEntity) {
            equiposMemoria.removeAll { it.id == equipo.id }
        }
        override suspend fun getById(id: Long): EquipoEntity? = equiposMemoria.find { it.id == id }
        override fun getByIdFlow(id: Long) = flowOf(equiposMemoria.find { it.id == id })
        override suspend fun getByCodigo(codigo: String): EquipoEntity? =
            equiposMemoria.find { it.codigoInterno.equals(codigo, ignoreCase = true) }
        override fun getAll() = flowOf(equiposMemoria.toList())
        override fun getPorEstado(estado: EstadoEquipo) = flowOf(equiposMemoria.filter { it.estado == estado })
        override fun getPorTipo(tipo: TipoEquipo) = flowOf(equiposMemoria.filter { it.tipo == tipo })
        override fun filtrarEquipos(query: String, tipo: TipoEquipo?, estado: EstadoEquipo?) =
            flowOf(equiposMemoria.filter {
                (tipo == null || it.tipo == tipo) &&
                (estado == null || it.estado == estado) &&
                (query.isEmpty() || it.codigoInterno.contains(query, true))
            })
        override fun getDisponiblesParaFechaYBloque(fecha: String, bloqueId: Long, tipo: TipoEquipo?) =
            flowOf(equiposMemoria.filter { it.estado == EstadoEquipo.DISPONIBLE })
        override suspend fun updateEstado(id: Long, nuevoEstado: EstadoEquipo) {
            val idx = equiposMemoria.indexOfFirst { it.id == id }
            if (idx >= 0) equiposMemoria[idx] = equiposMemoria[idx].copy(estado = nuevoEstado)
        }
        override suspend fun count(): Int = equiposMemoria.size
        override fun countPorEstadoFlow(estado: EstadoEquipo) = flowOf(equiposMemoria.count { it.estado == estado })
        override suspend fun countPorEstado(estado: EstadoEquipo): Int = equiposMemoria.count { it.estado == estado }
    }

    private val fakeReservaDao = object : ReservaDao {
        override suspend fun insert(reserva: ReservaEntity): Long {
            val id = (reservasMemoria.size + 1).toLong()
            val nueva = reserva.copy(id = id)
            reservasMemoria.add(nueva)
            return id
        }
        override suspend fun update(reserva: ReservaEntity) {
            val idx = reservasMemoria.indexOfFirst { it.id == reserva.id }
            if (idx >= 0) reservasMemoria[idx] = reserva
        }
        override suspend fun getById(id: Long): ReservaEntity? = reservasMemoria.find { it.id == id }
        override suspend fun getByIdConDetalle(id: Long) = null
        override fun getReservasPorDocente(docenteId: Long) = flowOf(emptyList<cl.duoc.prestaaula.data.local.relation.ReservaConDetalle>())
        override fun getReservasActivasPorDocente(docenteId: Long) = flowOf(emptyList<cl.duoc.prestaaula.data.local.relation.ReservaConDetalle>())
        override fun getReservasHoyPorDocente(docenteId: Long, fechaHoy: String) = flowOf(emptyList<cl.duoc.prestaaula.data.local.relation.ReservaConDetalle>())
        override suspend fun getReservaConflicto(equipoId: Long, fecha: String, bloqueId: Long): ReservaEntity? {
            return reservasMemoria.find {
                it.equipoId == equipoId && it.fecha == fecha && it.bloqueId == bloqueId &&
                (it.estado == EstadoReserva.ACTIVA || it.estado == EstadoReserva.RETIRADA)
            }
        }
        override suspend fun countReservasActivasDocenteEnBloque(docenteId: Long, fecha: String, bloqueId: Long): Int {
            return reservasMemoria.count {
                it.docenteId == docenteId && it.fecha == fecha && it.bloqueId == bloqueId && it.estado == EstadoReserva.ACTIVA
            }
        }
        override suspend fun getReservasVencidas(fechaHoy: String, horaActual: String): List<ReservaEntity> = emptyList()
        override suspend fun updateEstado(id: Long, nuevoEstado: EstadoReserva) {
            val idx = reservasMemoria.indexOfFirst { it.id == id }
            if (idx >= 0) reservasMemoria[idx] = reservasMemoria[idx].copy(estado = nuevoEstado)
        }
        override suspend fun countPorEstado(estado: EstadoReserva): Int = reservasMemoria.count { it.estado == estado }
    }

    private val fakePrestamoDao = object : PrestamoDao {
        override suspend fun insert(prestamo: PrestamoEntity): Long {
            val id = (prestamosMemoria.size + 1).toLong()
            val nuevo = prestamo.copy(id = id)
            prestamosMemoria.add(nuevo)
            return id
        }
        override suspend fun update(prestamo: PrestamoEntity) {
            val idx = prestamosMemoria.indexOfFirst { it.id == prestamo.id }
            if (idx >= 0) prestamosMemoria[idx] = prestamo
        }
        override suspend fun getById(id: Long): PrestamoEntity? = prestamosMemoria.find { it.id == id }
        override suspend fun getByIdConDetalle(id: Long) = null
        override suspend fun getPrestamoActivoPorEquipo(equipoId: Long): PrestamoEntity? =
            prestamosMemoria.find { it.equipoId == equipoId && it.fechaHoraDevolucion == null }
        override fun getPrestamosActivos() = flowOf(emptyList<cl.duoc.prestaaula.data.local.relation.PrestamoConDetalle>())
        override fun getPrestamosActivosPorDocente(docenteId: Long) = flowOf(emptyList<cl.duoc.prestaaula.data.local.relation.PrestamoConDetalle>())
        override fun getHistorialPorEquipo(equipoId: Long) = flowOf(emptyList<cl.duoc.prestaaula.data.local.relation.PrestamoConDetalle>())
        override suspend fun getUltimoPrestamoPorEquipo(equipoId: Long) = null
        override fun getAllHistorial() = flowOf(emptyList<cl.duoc.prestaaula.data.local.relation.PrestamoConDetalle>())
        override fun countPrestamosActivosFlow() = flowOf(prestamosMemoria.count { it.fechaHoraDevolucion == null })
        override fun getEquiposMasPrestados(limit: Int) = flowOf(emptyList<cl.duoc.prestaaula.data.local.relation.EquipoUsoStats>())
        override fun getDocentesConMasIncidencias(limit: Int) = flowOf(emptyList<cl.duoc.prestaaula.data.local.relation.DocenteIncidenciaStats>())
        override fun countIncidentesGravesFlow() = flowOf(0)
    }

    private val directTransactionRunner = DirectTransactionRunner()

    @Before
    fun setUp() {
        equiposMemoria.clear()
        reservasMemoria.clear()
        prestamosMemoria.clear()

        // Equipo 1: Disponible
        equiposMemoria.add(
            EquipoEntity(1L, TipoEquipo.PROYECTOR, "Epson", "PowerLite X49", "PROY-001", EstadoEquipo.DISPONIBLE)
        )
        // Equipo 2: En Mantención
        equiposMemoria.add(
            EquipoEntity(2L, TipoEquipo.NOTEBOOK, "Lenovo", "ThinkPad E14", "NB-005", EstadoEquipo.EN_MANTENCION)
        )
        // Equipo 3: De Baja
        equiposMemoria.add(
            EquipoEntity(3L, TipoEquipo.TABLET, "Samsung", "Tab A", "TAB-999", EstadoEquipo.DE_BAJA)
        )
    }

    @Test
    fun reserva_noPermiteReservarEquipoEnMantencion() = runBlocking {
        val repo = ReservaRepositoryImpl(
            reservaDao = fakeReservaDao,
            equipoDao = fakeEquipoDao,
            transactionRunner = directTransactionRunner
        )

        val reserva = ReservaEntity(
            equipoId = 2L, // NB-005 en mantención
            docenteId = 1L,
            curso = "3°B",
            fecha = "2026-10-25",
            bloqueId = 1L
        )

        val result = repo.crearReserva(reserva)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("no se puede reservar") == true)
    }

    @Test
    fun reserva_noPermiteConflictoDeDosReservasMismoEquipoYBloque() = runBlocking {
        val repo = ReservaRepositoryImpl(
            reservaDao = fakeReservaDao,
            equipoDao = fakeEquipoDao,
            transactionRunner = directTransactionRunner
        )

        val reserva1 = ReservaEntity(
            equipoId = 1L,
            docenteId = 1L,
            curso = "3°B",
            fecha = "2026-10-25",
            bloqueId = 1L
        )
        val res1 = repo.crearReserva(reserva1)
        assertTrue(res1.isSuccess)

        // Intento de reserva del mismo equipo por otro docente para el mismo día y bloque
        val reserva2 = ReservaEntity(
            equipoId = 1L,
            docenteId = 2L,
            curso = "1°A",
            fecha = "2026-10-25",
            bloqueId = 1L
        )
        val res2 = repo.crearReserva(reserva2)
        assertTrue(res2.isFailure)
        assertTrue(res2.exceptionOrNull()?.message?.contains("ya está reservado") == true)
    }

    @Test
    fun reserva_noPermiteMasDeDosReservasActivasParaMismoDocenteEnMismoBloque() = runBlocking {
        // Agregamos otros equipos disponibles
        equiposMemoria.add(EquipoEntity(4L, TipoEquipo.NOTEBOOK, "HP", "ProBook", "NB-004", EstadoEquipo.DISPONIBLE))
        equiposMemoria.add(EquipoEntity(5L, TipoEquipo.TABLET, "Apple", "iPad", "TAB-003", EstadoEquipo.DISPONIBLE))

        val repo = ReservaRepositoryImpl(
            reservaDao = fakeReservaDao,
            equipoDao = fakeEquipoDao,
            transactionRunner = directTransactionRunner
        )

        // Reserva 1 del docente 1
        val r1 = repo.crearReserva(ReservaEntity(equipoId = 1L, docenteId = 1L, curso = "3°B", fecha = "2026-10-25", bloqueId = 1L))
        assertTrue(r1.isSuccess)

        // Reserva 2 del docente 1
        val r2 = repo.crearReserva(ReservaEntity(equipoId = 4L, docenteId = 1L, curso = "3°B", fecha = "2026-10-25", bloqueId = 1L))
        assertTrue(r2.isSuccess)

        // Reserva 3 del docente 1 para el mismo bloque -> debe fallar
        val r3 = repo.crearReserva(ReservaEntity(equipoId = 5L, docenteId = 1L, curso = "3°B", fecha = "2026-10-25", bloqueId = 1L))
        assertTrue(r3.isFailure)
        assertTrue(r3.exceptionOrNull()?.message?.contains("límite máximo de 2 reservas") == true)
    }

    @Test
    fun prestamo_actualizaEquipoAPrestadoYReservaARetirada() = runBlocking {
        val repoReserva = ReservaRepositoryImpl(fakeReservaDao, fakeEquipoDao, directTransactionRunner)
        val repoPrestamo = PrestamoRepositoryImpl(fakePrestamoDao, fakeEquipoDao, fakeReservaDao, directTransactionRunner)

        val reservaId = repoReserva.crearReserva(
            ReservaEntity(equipoId = 1L, docenteId = 1L, curso = "3°B", fecha = "2026-10-25", bloqueId = 1L)
        ).getOrThrow()

        val prestamoId = repoPrestamo.registrarPrestamo(
            PrestamoEntity(equipoId = 1L, docenteId = 1L, reservaId = reservaId, curso = "3°B")
        ).getOrThrow()

        assertTrue(prestamoId > 0)
        assertEquals(EstadoEquipo.PRESTADO, fakeEquipoDao.getById(1L)?.estado)
        assertEquals(EstadoReserva.RETIRADA, fakeReservaDao.getById(reservaId)?.estado)
    }

    @Test
    fun devolucion_pasaEquipoAMantencionSiVieneDanado() = runBlocking {
        val repoPrestamo = PrestamoRepositoryImpl(fakePrestamoDao, fakeEquipoDao, fakeReservaDao, directTransactionRunner)

        val prestamoId = repoPrestamo.registrarPrestamo(
            PrestamoEntity(equipoId = 1L, docenteId = 1L, curso = "3°B")
        ).getOrThrow()

        val resDev = repoPrestamo.registrarDevolucion(
            prestamoId = prestamoId,
            estadoAlDevolver = EstadoDevolucion.DANADO,
            observaciones = "Lente rayado durante la clase",
            rutaFotoEvidencia = "/data/images/evidencia1.jpg",
            encargadoRecibioId = 3L
        )

        assertTrue(resDev.isSuccess)
        assertEquals(EstadoEquipo.EN_MANTENCION, fakeEquipoDao.getById(1L)?.estado)
    }

    @Test
    fun devolucion_exigeObservacionSiEstadoEsDanadoOIncompleto() = runBlocking {
        val repo = PrestamoRepositoryImpl(
            prestamoDao = fakePrestamoDao,
            equipoDao = fakeEquipoDao,
            reservaDao = fakeReservaDao,
            transactionRunner = directTransactionRunner
        )

        // Devolución con daño sin observación
        val resultSinObs = repo.registrarDevolucion(
            prestamoId = 1L,
            estadoAlDevolver = EstadoDevolucion.DANADO,
            observaciones = "",
            rutaFotoEvidencia = null,
            encargadoRecibioId = 3L
        )
        assertTrue(resultSinObs.isFailure)
        assertTrue(resultSinObs.exceptionOrNull()?.message?.contains("observación es obligatoria") == true)
    }
}
