package cl.duoc.prestaaula.domain.repository

import cl.duoc.prestaaula.data.datastore.UserSession
import cl.duoc.prestaaula.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentSession: Flow<UserSession>
    suspend fun login(email: String, rawPassword: String): Result<UsuarioEntity>
    suspend fun register(usuario: UsuarioEntity, rawPassword: String): Result<Long>
    suspend fun logout()
    fun getUsuarioById(id: Long): Flow<UsuarioEntity?>
    fun getDocentes(): Flow<List<UsuarioEntity>>
    suspend fun getUsuarioByRut(rut: String): UsuarioEntity?
}
