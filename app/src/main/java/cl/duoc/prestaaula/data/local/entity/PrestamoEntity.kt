package cl.duoc.prestaaula.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import cl.duoc.prestaaula.domain.model.EstadoDevolucion

@Entity(
    tableName = "prestamos",
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
        )
    ],
    indices = [
        Index("equipoId"),
        Index("docenteId"),
        Index("reservaId")
    ]
)
data class PrestamoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val equipoId: Long,
    val docenteId: Long,
    val reservaId: Long? = null,
    val curso: String,
    val fechaHoraRetiro: Long = System.currentTimeMillis(),
    val fechaHoraDevolucion: Long? = null,
    val estadoAlDevolver: EstadoDevolucion? = null,
    val observaciones: String = "",
    val rutaFotoEvidencia: String? = null,
    val encargadoRecibioId: Long? = null
)
