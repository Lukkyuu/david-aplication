package cl.duoc.prestaaula.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cl.duoc.prestaaula.data.local.entity.BloqueHorarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BloqueHorarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bloques: List<BloqueHorarioEntity>): List<Long>

    @Query("SELECT * FROM bloques_horarios ORDER BY numeroBloque ASC")
    fun getAll(): Flow<List<BloqueHorarioEntity>>

    @Query("SELECT * FROM bloques_horarios ORDER BY numeroBloque ASC")
    suspend fun getAllSync(): List<BloqueHorarioEntity>

    @Query("SELECT * FROM bloques_horarios WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): BloqueHorarioEntity?

    @Query("SELECT COUNT(*) FROM bloques_horarios")
    suspend fun count(): Int
}
