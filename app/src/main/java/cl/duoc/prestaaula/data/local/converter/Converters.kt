package cl.duoc.prestaaula.data.local.converter

import androidx.room.TypeConverter
import cl.duoc.prestaaula.domain.model.DiaSemana
import cl.duoc.prestaaula.domain.model.EstadoDevolucion
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.model.EstadoReserva
import cl.duoc.prestaaula.domain.model.RolUsuario
import cl.duoc.prestaaula.domain.model.TipoEquipo

class Converters {

    @TypeConverter
    fun fromRolUsuario(value: RolUsuario?): String? = value?.name

    @TypeConverter
    fun toRolUsuario(value: String?): RolUsuario? =
        value?.let { runCatching { enumValueOf<RolUsuario>(it) }.getOrNull() }

    @TypeConverter
    fun fromTipoEquipo(value: TipoEquipo?): String? = value?.name

    @TypeConverter
    fun toTipoEquipo(value: String?): TipoEquipo? =
        value?.let { runCatching { enumValueOf<TipoEquipo>(it) }.getOrNull() }

    @TypeConverter
    fun fromEstadoEquipo(value: EstadoEquipo?): String? = value?.name

    @TypeConverter
    fun toEstadoEquipo(value: String?): EstadoEquipo? =
        value?.let { runCatching { enumValueOf<EstadoEquipo>(it) }.getOrNull() }

    @TypeConverter
    fun fromEstadoReserva(value: EstadoReserva?): String? = value?.name

    @TypeConverter
    fun toEstadoReserva(value: String?): EstadoReserva? =
        value?.let { runCatching { enumValueOf<EstadoReserva>(it) }.getOrNull() }

    @TypeConverter
    fun fromEstadoDevolucion(value: EstadoDevolucion?): String? = value?.name

    @TypeConverter
    fun toEstadoDevolucion(value: String?): EstadoDevolucion? =
        value?.let { runCatching { enumValueOf<EstadoDevolucion>(it) }.getOrNull() }

    @TypeConverter
    fun fromDiaSemana(value: DiaSemana?): String? = value?.name

    @TypeConverter
    fun toDiaSemana(value: String?): DiaSemana? =
        value?.let { runCatching { enumValueOf<DiaSemana>(it) }.getOrNull() }
}
