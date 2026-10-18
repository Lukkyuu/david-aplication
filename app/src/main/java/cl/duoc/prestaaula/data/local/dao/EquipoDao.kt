package cl.duoc.prestaaula.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cl.duoc.prestaaula.data.local.entity.EquipoEntity
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.model.TipoEquipo
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(equipo: EquipoEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(equipos: List<EquipoEntity>): List<Long>

    @Update
    suspend fun update(equipo: EquipoEntity)

    @Delete
    suspend fun delete(equipo: EquipoEntity)

    @Query("SELECT * FROM equipos WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): EquipoEntity?

    @Query("SELECT * FROM equipos WHERE id = :id LIMIT 1")
    fun getByIdFlow(id: Long): Flow<EquipoEntity?>

    @Query("SELECT * FROM equipos WHERE UPPER(codigoInterno) = UPPER(:codigo) LIMIT 1")
    suspend fun getByCodigo(codigo: String): EquipoEntity?

    @Query("SELECT * FROM equipos ORDER BY tipo ASC, codigoInterno ASC")
    fun getAll(): Flow<List<EquipoEntity>>

    @Query("SELECT * FROM equipos WHERE estado = :estado ORDER BY codigoInterno ASC")
    fun getPorEstado(estado: EstadoEquipo): Flow<List<EquipoEntity>>

    @Query("SELECT * FROM equipos WHERE tipo = :tipo ORDER BY codigoInterno ASC")
    fun getPorTipo(tipo: TipoEquipo): Flow<List<EquipoEntity>>

    @Query("""
        SELECT * FROM equipos 
        WHERE (:tipo IS NULL OR tipo = :tipo)
          AND (:estado IS NULL OR estado = :estado)
          AND (:query = '' OR codigoInterno LIKE '%' || :query || '%' OR marca LIKE '%' || :query || '%' OR modelo LIKE '%' || :query || '%')
        ORDER BY codigoInterno ASC
    """)
    fun filtrarEquipos(query: String, tipo: TipoEquipo?, estado: EstadoEquipo?): Flow<List<EquipoEntity>>

    @Query("""
        SELECT * FROM equipos
        WHERE estado NOT IN ('EN_MANTENCION', 'DE_BAJA')
          AND (:tipo IS NULL OR tipo = :tipo)
          AND id NOT IN (
              SELECT equipoId FROM reservas
              WHERE fecha = :fecha
                AND bloqueId = :bloqueId
                AND estado IN ('ACTIVA', 'RETIRADA')
          )
        ORDER BY tipo ASC, codigoInterno ASC
    """)
    fun getDisponiblesParaFechaYBloque(fecha: String, bloqueId: Long, tipo: TipoEquipo? = null): Flow<List<EquipoEntity>>

    @Query("UPDATE equipos SET estado = :nuevoEstado WHERE id = :id")
    suspend fun updateEstado(id: Long, nuevoEstado: EstadoEquipo)

    @Query("SELECT COUNT(*) FROM equipos")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM equipos WHERE estado = :estado")
    fun countPorEstadoFlow(estado: EstadoEquipo): Flow<Int>

    @Query("SELECT COUNT(*) FROM equipos WHERE estado = :estado")
    suspend fun countPorEstado(estado: EstadoEquipo): Int
}
