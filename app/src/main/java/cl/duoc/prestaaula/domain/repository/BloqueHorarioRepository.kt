package cl.duoc.prestaaula.domain.repository

import cl.duoc.prestaaula.data.local.entity.BloqueHorarioEntity
import cl.duoc.prestaaula.data.local.entity.DisponibilidadDocenteEntity
import cl.duoc.prestaaula.data.local.relation.DisponibilidadConBloque
import cl.duoc.prestaaula.domain.model.DiaSemana
import kotlinx.coroutines.flow.Flow

interface BloqueHorarioRepository {
    fun getBloques(): Flow<List<BloqueHorarioEntity>>
    suspend fun getBloquesSync(): List<BloqueHorarioEntity>
    suspend fun getBloqueById(id: Long): BloqueHorarioEntity?
    fun getHorarioDocente(docenteId: Long): Flow<List<DisponibilidadConBloque>>
    fun getBloquesDocentePorDia(docenteId: Long, diaSemana: DiaSemana): Flow<List<DisponibilidadConBloque>>
    suspend fun guardarHorarioDocente(docenteId: Long, bloques: List<DisponibilidadDocenteEntity>)
}
