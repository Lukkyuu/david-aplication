package cl.duoc.prestaaula.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import cl.duoc.prestaaula.domain.model.DiaSemana

@Entity(
    tableName = "disponibilidad_docente",
    foreignKeys = [
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
        Index("docenteId"),
        Index("bloqueId"),
        Index(value = ["docenteId", "diaSemana", "bloqueId"], unique = true)
    ]
)
data class DisponibilidadDocenteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val docenteId: Long,
    val diaSemana: DiaSemana,
    val bloqueId: Long
)
