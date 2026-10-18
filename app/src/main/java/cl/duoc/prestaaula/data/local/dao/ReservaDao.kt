package cl.duoc.prestaaula.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import cl.duoc.prestaaula.data.local.entity.ReservaEntity
import cl.duoc.prestaaula.data.local.relation.ReservaConDetalle
import cl.duoc.prestaaula.domain.model.EstadoReserva
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservaDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(reserva: ReservaEntity): Long

    @Update
    suspend fun update(reserva: ReservaEntity)

    @Query("SELECT * FROM reservas WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): ReservaEntity?

    @Transaction
    @Query("SELECT * FROM reservas WHERE id = :id LIMIT 1")
    suspend fun getByIdConDetalle(id: Long): ReservaConDetalle?

    @Transaction
    @Query("""
        SELECT * FROM reservas 
        WHERE docenteId = :docenteId 
        ORDER BY fecha DESC, bloqueId DESC
    """)
    fun getReservasPorDocente(docenteId: Long): Flow<List<ReservaConDetalle>>

    @Transaction
    @Query("""
        SELECT * FROM reservas 
        WHERE docenteId = :docenteId AND estado = 'ACTIVA'
        ORDER BY fecha ASC, bloqueId ASC
    """)
    fun getReservasActivasPorDocente(docenteId: Long): Flow<List<ReservaConDetalle>>

    @Transaction
    @Query("""
        SELECT * FROM reservas 
        WHERE docenteId = :docenteId AND fecha = :fechaHoy AND estado = 'ACTIVA'
        ORDER BY bloqueId ASC
    """)
    fun getReservasHoyPorDocente(docenteId: Long, fechaHoy: String): Flow<List<ReservaConDetalle>>

    @Query("""
        SELECT * FROM reservas 
        WHERE equipoId = :equipoId 
          AND fecha = :fecha 
          AND bloqueId = :bloqueId 
          AND estado IN ('ACTIVA', 'RETIRADA')
        LIMIT 1
    """)
    suspend fun getReservaConflicto(equipoId: Long, fecha: String, bloqueId: Long): ReservaEntity?

    @Query("""
        SELECT COUNT(*) FROM reservas 
        WHERE docenteId = :docenteId 
          AND fecha = :fecha 
          AND bloqueId = :bloqueId 
          AND estado = 'ACTIVA'
    """)
    suspend fun countReservasActivasDocenteEnBloque(docenteId: Long, fecha: String, bloqueId: Long): Int

    @Transaction
    @Query("""
        SELECT r.* FROM reservas r
        INNER JOIN bloques_horarios b ON r.bloqueId = b.id
        WHERE r.estado = 'ACTIVA'
          AND (r.fecha < :fechaHoy OR (r.fecha = :fechaHoy AND b.horaFin < :horaActual))
    """)
    suspend fun getReservasVencidas(fechaHoy: String, horaActual: String): List<ReservaEntity>

    @Query("UPDATE reservas SET estado = :nuevoEstado WHERE id = :id")
    suspend fun updateEstado(id: Long, nuevoEstado: EstadoReserva)

    @Query("SELECT COUNT(*) FROM reservas WHERE estado = :estado")
    suspend fun countPorEstado(estado: EstadoReserva): Int
}
