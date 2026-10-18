package cl.duoc.prestaaula.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import cl.duoc.prestaaula.data.local.entity.PrestamoEntity
import cl.duoc.prestaaula.data.local.relation.DocenteIncidenciaStats
import cl.duoc.prestaaula.data.local.relation.EquipoUsoStats
import cl.duoc.prestaaula.data.local.relation.PrestamoConDetalle
import kotlinx.coroutines.flow.Flow

@Dao
interface PrestamoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(prestamo: PrestamoEntity): Long

    @Update
    suspend fun update(prestamo: PrestamoEntity)

    @Query("SELECT * FROM prestamos WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PrestamoEntity?

    @Transaction
    @Query("SELECT * FROM prestamos WHERE id = :id LIMIT 1")
    suspend fun getByIdConDetalle(id: Long): PrestamoConDetalle?

    @Query("SELECT * FROM prestamos WHERE equipoId = :equipoId AND fechaHoraDevolucion IS NULL LIMIT 1")
    suspend fun getPrestamoActivoPorEquipo(equipoId: Long): PrestamoEntity?

    @Transaction
    @Query("SELECT * FROM prestamos WHERE fechaHoraDevolucion IS NULL ORDER BY fechaHoraRetiro DESC")
    fun getPrestamosActivos(): Flow<List<PrestamoConDetalle>>

    @Transaction
    @Query("SELECT * FROM prestamos WHERE docenteId = :docenteId AND fechaHoraDevolucion IS NULL ORDER BY fechaHoraRetiro DESC")
    fun getPrestamosActivosPorDocente(docenteId: Long): Flow<List<PrestamoConDetalle>>

    @Transaction
    @Query("SELECT * FROM prestamos WHERE equipoId = :equipoId ORDER BY fechaHoraRetiro DESC")
    fun getHistorialPorEquipo(equipoId: Long): Flow<List<PrestamoConDetalle>>

    @Transaction
    @Query("SELECT * FROM prestamos WHERE equipoId = :equipoId ORDER BY fechaHoraRetiro DESC LIMIT 1")
    suspend fun getUltimoPrestamoPorEquipo(equipoId: Long): PrestamoConDetalle?

    @Transaction
    @Query("SELECT * FROM prestamos ORDER BY fechaHoraRetiro DESC")
    fun getAllHistorial(): Flow<List<PrestamoConDetalle>>

    @Query("SELECT COUNT(*) FROM prestamos WHERE fechaHoraDevolucion IS NULL")
    fun countPrestamosActivosFlow(): Flow<Int>

    @Query("""
        SELECT e.id AS equipoId, e.codigoInterno, e.tipo, e.marca, e.modelo, COUNT(p.id) AS totalPrestamos
        FROM equipos e
        INNER JOIN prestamos p ON e.id = p.equipoId
        GROUP BY e.id
        ORDER BY totalPrestamos DESC
        LIMIT :limit
    """)
    fun getEquiposMasPrestados(limit: Int = 5): Flow<List<EquipoUsoStats>>

    @Query("""
        SELECT u.id AS docenteId, u.nombre, u.apellido, u.email, COUNT(p.id) AS totalIncidentes
        FROM usuarios u
        INNER JOIN prestamos p ON u.id = p.docenteId
        WHERE p.estadoAlDevolver IN ('DANADO', 'INCOMPLETO', 'CON_OBSERVACIONES')
        GROUP BY u.id
        ORDER BY totalIncidentes DESC
        LIMIT :limit
    """)
    fun getDocentesConMasIncidencias(limit: Int = 5): Flow<List<DocenteIncidenciaStats>>

    @Query("SELECT COUNT(*) FROM prestamos WHERE estadoAlDevolver IN ('DANADO', 'INCOMPLETO')")
    fun countIncidentesGravesFlow(): Flow<Int>
}
