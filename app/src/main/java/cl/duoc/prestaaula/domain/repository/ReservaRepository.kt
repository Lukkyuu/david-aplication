package cl.duoc.prestaaula.domain.repository

import cl.duoc.prestaaula.data.local.entity.ReservaEntity
import cl.duoc.prestaaula.data.local.relation.ReservaConDetalle
import kotlinx.coroutines.flow.Flow

interface ReservaRepository {
    suspend fun crearReserva(reserva: ReservaEntity): Result<Long>
    suspend fun cancelarReserva(reservaId: Long): Result<Unit>
    fun getReservasPorDocente(docenteId: Long): Flow<List<ReservaConDetalle>>
    fun getReservasActivasPorDocente(docenteId: Long): Flow<List<ReservaConDetalle>>
    fun getReservasHoyPorDocente(docenteId: Long, fechaHoy: String): Flow<List<ReservaConDetalle>>
    suspend fun verificarConflictoReserva(equipoId: Long, fecha: String, bloqueId: Long): Boolean
    suspend fun countReservasActivasDocenteEnBloque(docenteId: Long, fecha: String, bloqueId: Long): Int
    suspend fun revisarYVencerReservas(fechaHoy: String, horaActual: String): Int
}
