package cl.duoc.prestaaula.ui.navigation

sealed class Screen(val route: String) {
    // Comunes
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Perfil : Screen("perfil")

    // Docente
    object DocenteHome : Screen("docente_home")
    object Disponibilidad : Screen("disponibilidad")
    object ReservaForm : Screen("reserva_form/{equipoId}") {
        fun createRoute(equipoId: Long = 0L) = "reserva_form/$equipoId"
    }
    object MisReservas : Screen("mis_reservas")
    object HorarioDocente : Screen("horario_docente")

    // CRA
    object CraDashboard : Screen("cra_dashboard")
    object Inventario : Screen("inventario")
    object EquipoForm : Screen("equipo_form?equipoId={equipoId}") {
        fun createRoute(equipoId: Long? = null) = if (equipoId != null) "equipo_form?equipoId=$equipoId" else "equipo_form"
    }
    object EquipoDetalle : Screen("equipo_detalle/{equipoId}") {
        fun createRoute(equipoId: Long) = "equipo_detalle/$equipoId"
    }
    object EquipoQr : Screen("equipo_qr/{equipoId}") {
        fun createRoute(equipoId: Long) = "equipo_qr/$equipoId"
    }
    object Prestamo : Screen("prestamo?codigo={codigo}") {
        fun createRoute(codigo: String? = null) = if (!codigo.isNullOrBlank()) "prestamo?codigo=$codigo" else "prestamo"
    }
    object Devolucion : Screen("devolucion?codigo={codigo}") {
        fun createRoute(codigo: String? = null) = if (!codigo.isNullOrBlank()) "devolucion?codigo=$codigo" else "devolucion"
    }
    object QrScanner : Screen("qr_scanner/{modo}") {
        fun createRoute(modo: String) = "qr_scanner/$modo"
    }

    // UTP / Finanzas / CRA
    object Reportes : Screen("reportes")
}
