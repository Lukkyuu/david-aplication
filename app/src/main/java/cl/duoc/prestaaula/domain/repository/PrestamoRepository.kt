package cl.duoc.prestaaula.domain.repository

import cl.duoc.prestaaula.data.local.entity.PrestamoEntity
import cl.duoc.prestaaula.data.local.relation.DocenteIncidenciaStats
import cl.duoc.prestaaula.data.local.relation.EquipoUsoStats
import cl.duoc.prestaaula.data.local.relation.PrestamoConDetalle
import cl.duoc.prestaaula.domain.model.EstadoDevolucion
import kotlinx.coroutines.flow.Flow

interface PrestamoRepository {
    suspend fun registrarPrestamo(prestamo: PrestamoEntity): Result<Long>
    suspend fun registrarDevolucion(
        prestamoId: Long,
        estadoAlDevolver: EstadoDevolucion,
        observaciones: String,
        rutaFotoEvidencia: String?,
        encargadoRecibioId: Long?
    ): Result<Unit>
    fun getPrestamosActivos(): Flow<List<PrestamoConDetalle>>
    fun getPrestamosActivosPorDocente(docenteId: Long): Flow<List<PrestamoConDetalle>>
    fun getHistorialPorEquipo(equipoId: Long): Flow<List<PrestamoConDetalle>>
    suspend fun getUltimoPrestamoPorEquipo(equipoId: Long): PrestamoConDetalle?
    fun getAllHistorial(): Flow<List<PrestamoConDetalle>>
    fun countPrestamosActivosFlow(): Flow<Int>
    fun getEquiposMasPrestados(limit: Int = 5): Flow<List<EquipoUsoStats>>
    fun getDocentesConMasIncidencias(limit: Int = 5): Flow<List<DocenteIncidenciaStats>>
}
