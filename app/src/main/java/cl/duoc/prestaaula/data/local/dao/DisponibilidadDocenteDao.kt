package cl.duoc.prestaaula.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import cl.duoc.prestaaula.data.local.entity.DisponibilidadDocenteEntity
import cl.duoc.prestaaula.data.local.relation.DisponibilidadConBloque
import cl.duoc.prestaaula.domain.model.DiaSemana
import kotlinx.coroutines.flow.Flow

@Dao
interface DisponibilidadDocenteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(disp: DisponibilidadDocenteEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(disps: List<DisponibilidadDocenteEntity>): List<Long>

    @Query("DELETE FROM disponibilidad_docente WHERE docenteId = :docenteId")
    suspend fun deletePorDocente(docenteId: Long)

    @Transaction
    suspend fun setHorarioDocente(docenteId: Long, nuevosBloques: List<DisponibilidadDocenteEntity>) {
        deletePorDocente(docenteId)
        insertAll(nuevosBloques)
    }

    @Transaction
    @Query("""
        SELECT * FROM disponibilidad_docente 
        WHERE docenteId = :docenteId 
        ORDER BY diaSemana ASC, bloqueId ASC
    """)
    fun getHorarioDocente(docenteId: Long): Flow<List<DisponibilidadConBloque>>

    @Transaction
    @Query("""
        SELECT * FROM disponibilidad_docente 
        WHERE docenteId = :docenteId AND diaSemana = :diaSemana
        ORDER BY bloqueId ASC
    """)
    fun getBloquesDocentePorDia(docenteId: Long, diaSemana: DiaSemana): Flow<List<DisponibilidadConBloque>>
}
