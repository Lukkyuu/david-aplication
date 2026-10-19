package cl.duoc.prestaaula.data.repository

import cl.duoc.prestaaula.data.datastore.UserSession
import cl.duoc.prestaaula.data.datastore.UserSessionDataStore
import cl.duoc.prestaaula.data.local.dao.UsuarioDao
import cl.duoc.prestaaula.data.local.entity.UsuarioEntity
import cl.duoc.prestaaula.data.seed.PasswordHasher
import cl.duoc.prestaaula.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class AuthRepositoryImpl(
    private val usuarioDao: UsuarioDao,
    private val sessionDataStore: UserSessionDataStore
) : AuthRepository {

    override val currentSession: Flow<UserSession> = sessionDataStore.userSession

    override suspend fun login(email: String, rawPassword: String): Result<UsuarioEntity> {
        val normalizedEmail = email.trim().lowercase()
        val usuario = usuarioDao.getByEmail(normalizedEmail)
            ?: return Result.failure(IllegalArgumentException("No existe ninguna cuenta registrada con este correo."))

        val passwordMatches = PasswordHasher.verify(rawPassword, usuario.passwordHash)
        if (!passwordMatches) {
            return Result.failure(IllegalArgumentException("Contraseña incorrecta. Intente nuevamente."))
        }

        sessionDataStore.saveSession(
            userId = usuario.id,
            rut = usuario.rut,
            nombre = usuario.nombre,
            apellido = usuario.apellido,
            email = usuario.email,
            rol = usuario.rol
        )

        return Result.success(usuario)
    }

    override suspend fun register(usuario: UsuarioEntity, rawPassword: String): Result<Long> {
        return try {
            val normalizedEmail = usuario.email.trim().lowercase()
            val existingByEmail = usuarioDao.getByEmail(normalizedEmail)
            if (existingByEmail != null) {
                return Result.failure(IllegalArgumentException("El correo ya se encuentra registrado."))
            }

            val existingByRut = usuarioDao.getByRut(usuario.rut.trim())
            if (existingByRut != null) {
                return Result.failure(IllegalArgumentException("El RUT ya se encuentra registrado."))
            }

            val hashedPassword = PasswordHasher.hash(rawPassword)
            val usuarioConHash = usuario.copy(
                email = normalizedEmail,
                passwordHash = hashedPassword
            )

            val newId = usuarioDao.insert(usuarioConHash)

            // Iniciar sesión automáticamente
            sessionDataStore.saveSession(
                userId = newId,
                rut = usuarioConHash.rut,
                nombre = usuarioConHash.nombre,
                apellido = usuarioConHash.apellido,
                email = usuarioConHash.email,
                rol = usuarioConHash.rol
            )

            Result.success(newId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        sessionDataStore.clearSession()
    }

    override fun getUsuarioById(id: Long): Flow<UsuarioEntity?> {
        return usuarioDao.getByIdFlow(id)
    }

    override fun getDocentes(): Flow<List<UsuarioEntity>> {
        return usuarioDao.getUsuariosPorRol(cl.duoc.prestaaula.domain.model.RolUsuario.DOCENTE)
    }

    override suspend fun getUsuarioByRut(rut: String): UsuarioEntity? {
        return usuarioDao.getByRut(rut.trim())
    }
}
