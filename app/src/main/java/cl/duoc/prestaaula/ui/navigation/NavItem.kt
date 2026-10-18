package cl.duoc.prestaaula.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.ui.graphics.vector.ImageVector
import cl.duoc.prestaaula.domain.model.RolUsuario

data class NavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

object RoleNavItems {
    fun getItemsForRole(rol: RolUsuario?): List<NavItem> {
        return when (rol) {
            RolUsuario.DOCENTE -> listOf(
                NavItem("Inicio", Screen.DocenteHome.route, Icons.Default.Home),
                NavItem("Disponibilidad", Screen.Disponibilidad.route, Icons.Default.CalendarMonth),
                NavItem("Mis Reservas", Screen.MisReservas.route, Icons.AutoMirrored.Filled.Assignment),
                NavItem("Mi Horario", Screen.HorarioDocente.route, Icons.Default.Schedule),
                NavItem("Perfil", Screen.Perfil.route, Icons.Default.Person)
            )
            RolUsuario.ENCARGADO_CRA -> listOf(
                NavItem("Panel", Screen.CraDashboard.route, Icons.Default.Dashboard),
                NavItem("Inventario", Screen.Inventario.route, Icons.Default.Devices),
                NavItem("Préstamo", Screen.Prestamo.route, Icons.Default.QrCodeScanner),
                NavItem("Devolución", Screen.Devolucion.route, Icons.Default.CheckCircle),
                NavItem("Reportes", Screen.Reportes.route, Icons.Default.BarChart),
                NavItem("Perfil", Screen.Perfil.route, Icons.Default.Person)
            )
            RolUsuario.UTP, RolUsuario.ADMIN_FINANZAS -> listOf(
                NavItem("Reportes", Screen.Reportes.route, Icons.Default.BarChart),
                NavItem("Perfil", Screen.Perfil.route, Icons.Default.Person)
            )
            null -> emptyList()
        }
    }
}
