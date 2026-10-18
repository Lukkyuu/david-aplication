package cl.duoc.prestaaula.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bloques_horarios")
data class BloqueHorarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val numeroBloque: Int,
    val horaInicio: String,
    val horaFin: String,
    val etiqueta: String
)
