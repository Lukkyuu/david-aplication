package cl.duoc.prestaaula.domain.repository

import cl.duoc.prestaaula.data.local.entity.EquipoEntity
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.model.TipoEquipo
import kotlinx.coroutines.flow.Flow

interface EquipoRepository {
    fun getEquipos(query: String = "", tipo: TipoEquipo? = null, estado: EstadoEquipo? = null): Flow<List<EquipoEntity>>
    fun getEquipoById(id: Long): Flow<EquipoEntity?>
    suspend fun getEquipoByIdSync(id: Long): EquipoEntity?
    suspend fun getEquipoByCodigo(codigo: String): EquipoEntity?
    fun getDisponiblesParaFechaYBloque(fecha: String, bloqueId: Long, tipo: TipoEquipo? = null): Flow<List<EquipoEntity>>
    suspend fun insertEquipo(equipo: EquipoEntity): Long
    suspend fun updateEquipo(equipo: EquipoEntity)
    suspend fun deleteEquipo(equipo: EquipoEntity)
    suspend fun updateEstado(id: Long, estado: EstadoEquipo)
    fun countPorEstadoFlow(estado: EstadoEquipo): Flow<Int>
}
