package cl.duoc.prestaaula.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import cl.duoc.prestaaula.data.local.entity.BloqueHorarioEntity
import cl.duoc.prestaaula.data.local.entity.DisponibilidadDocenteEntity
import cl.duoc.prestaaula.data.local.entity.EquipoEntity
import cl.duoc.prestaaula.data.local.entity.PrestamoEntity
import cl.duoc.prestaaula.data.local.entity.ReservaEntity
import cl.duoc.prestaaula.data.local.entity.UsuarioEntity
import cl.duoc.prestaaula.domain.model.TipoEquipo

data class ReservaConDetalle(
    @Embedded val reserva: ReservaEntity,
    @Relation(parentColumn = "equipoId", entityColumn = "id")
    val equipo: EquipoEntity,
    @Relation(parentColumn = "docenteId", entityColumn = "id")
    val docente: UsuarioEntity,
    @Relation(parentColumn = "bloqueId", entityColumn = "id")
    val bloque: BloqueHorarioEntity
)

data class PrestamoConDetalle(
    @Embedded val prestamo: PrestamoEntity,
    @Relation(parentColumn = "equipoId", entityColumn = "id")
    val equipo: EquipoEntity,
    @Relation(parentColumn = "docenteId", entityColumn = "id")
    val docente: UsuarioEntity,
    @Relation(parentColumn = "reservaId", entityColumn = "id")
    val reserva: ReservaEntity? = null,
    @Relation(parentColumn = "encargadoRecibioId", entityColumn = "id")
    val encargado: UsuarioEntity? = null
)

data class DisponibilidadConBloque(
    @Embedded val disponibilidad: DisponibilidadDocenteEntity,
    @Relation(parentColumn = "bloqueId", entityColumn = "id")
    val bloque: BloqueHorarioEntity
)

data class EquipoUsoStats(
    val equipoId: Long,
    val codigoInterno: String,
    val tipo: TipoEquipo,
    val marca: String,
    val modelo: String,
    val totalPrestamos: Int
)

data class DocenteIncidenciaStats(
    val docenteId: Long,
    val nombre: String,
    val apellido: String,
    val email: String,
    val totalIncidentes: Int
)

data class PrestamoMensualStats(
    val periodo: String, // ej: "2026-10"
    val total: Int
)
