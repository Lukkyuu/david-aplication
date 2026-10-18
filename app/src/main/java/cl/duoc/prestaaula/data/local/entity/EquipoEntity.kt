package cl.duoc.prestaaula.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.model.TipoEquipo

@Entity(
    tableName = "equipos",
    indices = [
        Index(value = ["codigoInterno"], unique = true)
    ]
)
data class EquipoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val tipo: TipoEquipo,
    val marca: String,
    val modelo: String,
    val codigoInterno: String,
    val estado: EstadoEquipo = EstadoEquipo.DISPONIBLE,
    val observaciones: String = "",
    val fotoUri: String? = null
)
