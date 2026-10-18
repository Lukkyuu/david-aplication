package cl.duoc.prestaaula.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.withTransaction
import cl.duoc.prestaaula.data.local.converter.Converters
import cl.duoc.prestaaula.data.local.dao.BloqueHorarioDao
import cl.duoc.prestaaula.data.local.dao.DisponibilidadDocenteDao
import cl.duoc.prestaaula.data.local.dao.EquipoDao
import cl.duoc.prestaaula.data.local.dao.PrestamoDao
import cl.duoc.prestaaula.data.local.dao.ReservaDao
import cl.duoc.prestaaula.data.local.dao.UsuarioDao
import cl.duoc.prestaaula.data.local.entity.BloqueHorarioEntity
import cl.duoc.prestaaula.data.local.entity.DisponibilidadDocenteEntity
import cl.duoc.prestaaula.data.local.entity.EquipoEntity
import cl.duoc.prestaaula.data.local.entity.PrestamoEntity
import cl.duoc.prestaaula.data.local.entity.ReservaEntity
import cl.duoc.prestaaula.data.local.entity.UsuarioEntity
import cl.duoc.prestaaula.domain.model.EstadoDevolucion
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.model.EstadoReserva

@Database(
    entities = [
        UsuarioEntity::class,
        EquipoEntity::class,
        BloqueHorarioEntity::class,
        ReservaEntity::class,
        PrestamoEntity::class,
        DisponibilidadDocenteEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PrestaAulaDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun equipoDao(): EquipoDao
    abstract fun bloqueHorarioDao(): BloqueHorarioDao
    abstract fun reservaDao(): ReservaDao
    abstract fun prestamoDao(): PrestamoDao
    abstract fun disponibilidadDocenteDao(): DisponibilidadDocenteDao

    /**
     * Registra un préstamo en una única transacción de Room:
     * 1. Inserta el préstamo.
     * 2. Si venía de una reserva previa, la pasa a RETIRADA.
     * 3. Pasa el equipo a PRESTADO.
     */
    suspend fun registrarPrestamoTransaccional(prestamo: PrestamoEntity): Long {
        return withTransaction {
            val prestamoId = prestamoDao().insert(prestamo)
            if (prestamo.reservaId != null && prestamo.reservaId > 0) {
                reservaDao().updateEstado(prestamo.reservaId, EstadoReserva.RETIRADA)
            }
            equipoDao().updateEstado(prestamo.equipoId, EstadoEquipo.PRESTADO)
            prestamoId
        }
    }

    /**
     * Registra la devolución en una única transacción de Room:
     * 1. Actualiza el préstamo con hora de devolución, estado, observación, foto y encargado.
     * 2. Si el estado es DANADO o INCOMPLETO, el equipo pasa automáticamente a EN_MANTENCION.
     * 3. Si el estado es BUENO o CON_OBSERVACIONES, el equipo vuelve a DISPONIBLE.
     */
    suspend fun registrarDevolucionTransaccional(
        prestamoId: Long,
        fechaHoraDevolucion: Long,
        estadoAlDevolver: EstadoDevolucion,
        observaciones: String,
        rutaFotoEvidencia: String?,
        encargadoRecibioId: Long?
    ) {
        withTransaction {
            val prestamoExistente = prestamoDao().getById(prestamoId)
                ?: throw IllegalStateException("El préstamo con id $prestamoId no existe")

            val prestamoActualizado = prestamoExistente.copy(
                fechaHoraDevolucion = fechaHoraDevolucion,
                estadoAlDevolver = estadoAlDevolver,
                observaciones = observaciones,
                rutaFotoEvidencia = rutaFotoEvidencia,
                encargadoRecibioId = encargadoRecibioId
            )
            prestamoDao().update(prestamoActualizado)

            val nuevoEstadoEquipo = when (estadoAlDevolver) {
                EstadoDevolucion.DANADO, EstadoDevolucion.INCOMPLETO -> EstadoEquipo.EN_MANTENCION
                EstadoDevolucion.BUENO, EstadoDevolucion.CON_OBSERVACIONES -> EstadoEquipo.DISPONIBLE
            }
            equipoDao().updateEstado(prestamoExistente.equipoId, nuevoEstadoEquipo)
        }
    }

    /**
     * Revisa todas las reservas ACTIVAS que ya hayan concluido su bloque y las pasa a VENCIDA.
     */
    suspend fun revisarYVencerReservas(fechaHoy: String, horaActual: String): Int {
        return withTransaction {
            val vencidas = reservaDao().getReservasVencidas(fechaHoy, horaActual)
            for (reserva in vencidas) {
                reservaDao().updateEstado(reserva.id, EstadoReserva.VENCIDA)
            }
            vencidas.size
        }
    }
}
