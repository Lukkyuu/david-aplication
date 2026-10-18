package cl.duoc.prestaaula.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import cl.duoc.prestaaula.domain.model.RolUsuario

@Entity(
    tableName = "usuarios",
    indices = [
        Index(value = ["email"], unique = true),
        Index(value = ["rut"], unique = true)
    ]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val rut: String,
    val nombre: String,
    val apellido: String,
    val email: String,
    val rol: RolUsuario,
    val passwordHash: String
)
