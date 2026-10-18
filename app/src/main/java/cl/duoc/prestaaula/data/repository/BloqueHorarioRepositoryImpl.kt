package cl.duoc.prestaaula.data.repository

import cl.duoc.prestaaula.data.local.dao.BloqueHorarioDao
import cl.duoc.prestaaula.data.local.dao.DisponibilidadDocenteDao
import cl.duoc.prestaaula.data.local.entity.BloqueHorarioEntity
import cl.duoc.prestaaula.data.local.entity.DisponibilidadDocenteEntity
import cl.duoc.prestaaula.data.local.relation.DisponibilidadConBloque
import cl.duoc.prestaaula.domain.model.DiaSemana
import cl.duoc.prestaaula.domain.repository.BloqueHorarioRepository
import kotlinx.coroutines.flow.Flow

class BloqueHorarioRepositoryImpl(
    private val bloqueHorarioDao: BloqueHorarioDao,
    private val disponibilidadDocenteDao: DisponibilidadDocenteDao
) : BloqueHorarioRepository {

    override fun getBloques(): Flow<List<BloqueHorarioEntity>> {
        return bloqueHorarioDao.getAll()
    }

    override suspend fun getBloquesSync(): List<BloqueHorarioEntity> {
        return bloqueHorarioDao.getAllSync()
    }

    override suspend fun getBloqueById(id: Long): BloqueHorarioEntity? {
        return bloqueHorarioDao.getById(id)
    }

    override fun getHorarioDocente(docenteId: Long): Flow<List<DisponibilidadConBloque>> {
        return disponibilidadDocenteDao.getHorarioDocente(docenteId)
    }

    override fun getBloquesDocentePorDia(docenteId: Long, diaSemana: DiaSemana): Flow<List<DisponibilidadConBloque>> {
        return disponibilidadDocenteDao.getBloquesDocentePorDia(docenteId, diaSemana)
    }

    override suspend fun guardarHorarioDocente(docenteId: Long, bloques: List<DisponibilidadDocenteEntity>) {
        disponibilidadDocenteDao.setHorarioDocente(docenteId, bloques)
    }
}
