package cl.duoc.prestaaula.domain.model

enum class RolUsuario(val displayName: String) {
    DOCENTE("Docente"),
    ENCARGADO_CRA("Encargado CRA"),
    UTP("Jefe UTP / Dirección"),
    ADMIN_FINANZAS("Administración y Finanzas")
}

enum class TipoEquipo(val displayName: String) {
    NOTEBOOK("Notebook"),
    TABLET("Tablet"),
    PROYECTOR("Proyector"),
    CABLE_HDMI("Cable HDMI"),
    OTRO("Otro")
}

enum class EstadoEquipo(val displayName: String) {
    DISPONIBLE("Disponible"),
    PRESTADO("Prestado"),
    EN_MANTENCION("En Mantención"),
    DE_BAJA("De Baja")
}

enum class EstadoReserva(val displayName: String) {
    ACTIVA("Activa"),
    RETIRADA("Retirada"),
    CANCELADA("Cancelada"),
    VENCIDA("Vencida")
}

enum class EstadoDevolucion(val displayName: String) {
    BUENO("Buen estado"),
    CON_OBSERVACIONES("Con observaciones"),
    DANADO("Dañado"),
    INCOMPLETO("Incompleto (falta pieza/cables)")
}

enum class DiaSemana(val displayName: String, val isoDay: Int) {
    LUNES("Lunes", 1),
    MARTES("Martes", 2),
    MIERCOLES("Miércoles", 3),
    JUEVES("Jueves", 4),
    VIERNES("Viernes", 5)
}
