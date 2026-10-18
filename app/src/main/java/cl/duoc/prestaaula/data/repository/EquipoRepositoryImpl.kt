package cl.duoc.prestaaula.data.repository

import cl.duoc.prestaaula.data.local.dao.EquipoDao
import cl.duoc.prestaaula.data.local.entity.EquipoEntity
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.model.TipoEquipo
import cl.duoc.prestaaula.domain.repository.EquipoRepository
import kotlinx.coroutines.flow.Flow

class EquipoRepositoryImpl(
    private val equipoDao: EquipoDao
) : EquipoRepository {

    override fun getEquipos(query: String, tipo: TipoEquipo?, estado: EstadoEquipo?): Flow<List<EquipoEntity>> {
        return equipoDao.filtrarEquipos(query.trim(), tipo, estado)
    }

    override fun getEquipoById(id: Long): Flow<EquipoEntity?> {
        return equipoDao.getByIdFlow(id)
    }

    override suspend fun getEquipoByIdSync(id: Long): EquipoEntity? {
        return equipoDao.getById(id)
    }

    override suspend fun getEquipoByCodigo(codigo: String): EquipoEntity? {
        return equipoDao.getByCodigo(codigo.trim())
    }

    override fun getDisponiblesParaFechaYBloque(fecha: String, bloqueId: Long, tipo: TipoEquipo?): Flow<List<EquipoEntity>> {
        return equipoDao.getDisponiblesParaFechaYBloque(fecha, bloqueId, tipo)
    }

    override suspend fun insertEquipo(equipo: EquipoEntity): Long {
        val existing = equipoDao.getByCodigo(equipo.codigoInterno.trim())
        if (existing != null) {
            throw IllegalArgumentException("Ya existe un equipo registrado con el código ${equipo.codigoInterno}.")
        }
        return equipoDao.insert(equipo)
    }

    override suspend fun updateEquipo(equipo: EquipoEntity) {
        equipoDao.update(equipo)
    }

    override suspend fun deleteEquipo(equipo: EquipoEntity) {
        equipoDao.delete(equipo)
    }

    override suspend fun updateEstado(id: Long, estado: EstadoEquipo) {
        equipoDao.updateEstado(id, estado)
    }

    override fun countPorEstadoFlow(estado: EstadoEquipo): Flow<Int> {
        return equipoDao.countPorEstadoFlow(estado)
    }
}
