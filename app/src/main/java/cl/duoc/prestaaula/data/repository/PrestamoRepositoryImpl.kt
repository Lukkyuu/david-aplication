package cl.duoc.prestaaula.data.repository

import cl.duoc.prestaaula.data.local.dao.EquipoDao
import cl.duoc.prestaaula.data.local.dao.PrestamoDao
import cl.duoc.prestaaula.data.local.dao.ReservaDao
import cl.duoc.prestaaula.data.local.entity.PrestamoEntity
import cl.duoc.prestaaula.data.local.relation.DocenteIncidenciaStats
import cl.duoc.prestaaula.data.local.relation.EquipoUsoStats
import cl.duoc.prestaaula.data.local.relation.PrestamoConDetalle
import cl.duoc.prestaaula.data.local.transaction.TransactionRunner
import cl.duoc.prestaaula.domain.model.EstadoDevolucion
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.model.EstadoReserva
import cl.duoc.prestaaula.domain.repository.PrestamoRepository
import kotlinx.coroutines.flow.Flow

class PrestamoRepositoryImpl(
    private val prestamoDao: PrestamoDao,
    private val equipoDao: EquipoDao,
    private val reservaDao: ReservaDao,
    private val transactionRunner: TransactionRunner
) : PrestamoRepository {

    override suspend fun registrarPrestamo(prestamo: PrestamoEntity): Result<Long> {
        val equipo = equipoDao.getById(prestamo.equipoId)
            ?: return Result.failure(IllegalArgumentException("El equipo especificado no existe."))

        if (equipo.estado == EstadoEquipo.PRESTADO) {
            return Result.failure(IllegalStateException("El equipo ya se encuentra prestado actualmente."))
        }
        if (equipo.estado == EstadoEquipo.EN_MANTENCION || equipo.estado == EstadoEquipo.DE_BAJA) {
            return Result.failure(IllegalStateException("El equipo está en estado '${equipo.estado.displayName}' y no se puede prestar."))
        }

        return try {
            val prestamoId = transactionRunner.runInTransaction {
                val id = prestamoDao.insert(prestamo)
                if (prestamo.reservaId != null && prestamo.reservaId > 0) {
                    reservaDao.updateEstado(prestamo.reservaId, EstadoReserva.RETIRADA)
                }
                equipoDao.updateEstado(prestamo.equipoId, EstadoEquipo.PRESTADO)
                id
            }
            Result.success(prestamoId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registrarDevolucion(
        prestamoId: Long,
        estadoAlDevolver: EstadoDevolucion,
        observaciones: String,
        rutaFotoEvidencia: String?,
        encargadoRecibioId: Long?
    ): Result<Unit> {
        if ((estadoAlDevolver == EstadoDevolucion.DANADO || estadoAlDevolver == EstadoDevolucion.INCOMPLETO)) {
            if (observaciones.trim().isBlank()) {
                return Result.failure(IllegalArgumentException("La observación es obligatoria cuando el equipo se devuelve dañado o incompleto."))
            }
        }

        return try {
            transactionRunner.runInTransaction {
                val prestamoExistente = prestamoDao.getById(prestamoId)
                    ?: throw IllegalStateException("El préstamo con id $prestamoId no existe")

                val prestamoActualizado = prestamoExistente.copy(
                    fechaHoraDevolucion = System.currentTimeMillis(),
                    estadoAlDevolver = estadoAlDevolver,
                    observaciones = observaciones.trim(),
                    rutaFotoEvidencia = rutaFotoEvidencia,
                    encargadoRecibioId = encargadoRecibioId
                )
                prestamoDao.update(prestamoActualizado)

                val nuevoEstadoEquipo = when (estadoAlDevolver) {
                    EstadoDevolucion.DANADO, EstadoDevolucion.INCOMPLETO -> EstadoEquipo.EN_MANTENCION
                    EstadoDevolucion.BUENO, EstadoDevolucion.CON_OBSERVACIONES -> EstadoEquipo.DISPONIBLE
                }
                equipoDao.updateEstado(prestamoExistente.equipoId, nuevoEstadoEquipo)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getPrestamosActivos(): Flow<List<PrestamoConDetalle>> {
        return prestamoDao.getPrestamosActivos()
    }

    override fun getPrestamosActivosPorDocente(docenteId: Long): Flow<List<PrestamoConDetalle>> {
        return prestamoDao.getPrestamosActivosPorDocente(docenteId)
    }

    override fun getHistorialPorEquipo(equipoId: Long): Flow<List<PrestamoConDetalle>> {
        return prestamoDao.getHistorialPorEquipo(equipoId)
    }

    override suspend fun getUltimoPrestamoPorEquipo(equipoId: Long): PrestamoConDetalle? {
        return prestamoDao.getUltimoPrestamoPorEquipo(equipoId)
    }

    override fun getAllHistorial(): Flow<List<PrestamoConDetalle>> {
        return prestamoDao.getAllHistorial()
    }

    override fun countPrestamosActivosFlow(): Flow<Int> {
        return prestamoDao.countPrestamosActivosFlow()
    }

    override fun getEquiposMasPrestados(limit: Int): Flow<List<EquipoUsoStats>> {
        return prestamoDao.getEquiposMasPrestados(limit)
    }

    override fun getDocentesConMasIncidencias(limit: Int): Flow<List<DocenteIncidenciaStats>> {
        return prestamoDao.getDocentesConMasIncidencias(limit)
    }
}
