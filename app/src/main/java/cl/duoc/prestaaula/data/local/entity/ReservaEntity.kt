package cl.duoc.prestaaula.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import cl.duoc.prestaaula.domain.model.EstadoReserva

@Entity(
    tableName = "reservas",
    foreignKeys = [
        ForeignKey(
            entity = EquipoEntity::class,
            parentColumns = ["id"],
            childColumns = ["equipoId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["docenteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BloqueHorarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["bloqueId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("equipoId"),
        Index("docenteId"),
        Index("bloqueId"),
        Index(value = ["equipoId", "fecha", "bloqueId"])
    ]
)
data class ReservaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val equipoId: Long,
    val docenteId: Long,
    val curso: String,
    val fecha: String, // Formato YYYY-MM-DD
    val bloqueId: Long,
    val estado: EstadoReserva = EstadoReserva.ACTIVA,
    val fechaCreacion: Long = System.currentTimeMillis(),
    val observaciones: String = ""
)
