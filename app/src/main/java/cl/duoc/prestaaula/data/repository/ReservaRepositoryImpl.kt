package cl.duoc.prestaaula.data.repository

import cl.duoc.prestaaula.data.local.dao.EquipoDao
import cl.duoc.prestaaula.data.local.dao.ReservaDao
import cl.duoc.prestaaula.data.local.entity.ReservaEntity
import cl.duoc.prestaaula.data.local.relation.ReservaConDetalle
import cl.duoc.prestaaula.data.local.transaction.TransactionRunner
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.model.EstadoReserva
import cl.duoc.prestaaula.domain.repository.ReservaRepository
import kotlinx.coroutines.flow.Flow

class ReservaRepositoryImpl(
    private val reservaDao: ReservaDao,
    private val equipoDao: EquipoDao,
    private val transactionRunner: TransactionRunner
) : ReservaRepository {

    override suspend fun crearReserva(reserva: ReservaEntity): Result<Long> {
        val equipo = equipoDao.getById(reserva.equipoId)
            ?: return Result.failure(IllegalArgumentException("El equipo solicitado no existe."))

        if (equipo.estado == EstadoEquipo.EN_MANTENCION || equipo.estado == EstadoEquipo.DE_BAJA) {
            return Result.failure(IllegalStateException("Este equipo está en estado '${equipo.estado.displayName}' y no se puede reservar."))
        }

        // Regla: No puede haber dos reservas activas del mismo equipo en la misma fecha y bloque
        val conflicto = reservaDao.getReservaConflicto(reserva.equipoId, reserva.fecha, reserva.bloqueId)
        if (conflicto != null) {
            return Result.failure(IllegalStateException("Este equipo ya está reservado para la fecha ${reserva.fecha} en el bloque seleccionado por otro docente."))
        }

        // Regla: Límite de reservas por bloque para el mismo docente (máximo 2)
        val reservasActivasDocente = reservaDao.countReservasActivasDocenteEnBloque(reserva.docenteId, reserva.fecha, reserva.bloqueId)
        if (reservasActivasDocente >= 2) {
            return Result.failure(IllegalStateException("Has alcanzado el límite máximo de 2 reservas activas para el mismo bloque."))
        }

        return try {
            val id = reservaDao.insert(reserva)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelarReserva(reservaId: Long): Result<Unit> {
        val reserva = reservaDao.getById(reservaId)
            ?: return Result.failure(IllegalArgumentException("La reserva no existe."))

        if (reserva.estado != EstadoReserva.ACTIVA) {
            return Result.failure(IllegalStateException("Solo se pueden cancelar reservas en estado ACTIVA."))
        }

        return try {
            reservaDao.updateEstado(reservaId, EstadoReserva.CANCELADA)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getReservasPorDocente(docenteId: Long): Flow<List<ReservaConDetalle>> {
        return reservaDao.getReservasPorDocente(docenteId)
    }

    override fun getReservasActivasPorDocente(docenteId: Long): Flow<List<ReservaConDetalle>> {
        return reservaDao.getReservasActivasPorDocente(docenteId)
    }

    override fun getReservasHoyPorDocente(docenteId: Long, fechaHoy: String): Flow<List<ReservaConDetalle>> {
        return reservaDao.getReservasHoyPorDocente(docenteId, fechaHoy)
    }

    override suspend fun verificarConflictoReserva(equipoId: Long, fecha: String, bloqueId: Long): Boolean {
        return reservaDao.getReservaConflicto(equipoId, fecha, bloqueId) != null
    }

    override suspend fun countReservasActivasDocenteEnBloque(docenteId: Long, fecha: String, bloqueId: Long): Int {
        return reservaDao.countReservasActivasDocenteEnBloque(docenteId, fecha, bloqueId)
    }

    override suspend fun revisarYVencerReservas(fechaHoy: String, horaActual: String): Int {
        return transactionRunner.runInTransaction {
            val vencidas = reservaDao.getReservasVencidas(fechaHoy, horaActual)
            for (reserva in vencidas) {
                reservaDao.updateEstado(reserva.id, EstadoReserva.VENCIDA)
            }
            vencidas.size
        }
    }
}
