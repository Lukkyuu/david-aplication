package cl.duoc.prestaaula.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
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
                NavItem("Reservas", Screen.MisReservas.route, Icons.Default.CalendarMonth),
                NavItem("Perfil", Screen.Perfil.route, Icons.Default.Person)
            )
            RolUsuario.ENCARGADO_CRA -> listOf(
                NavItem("Panel", Screen.CraDashboard.route, Icons.Default.Dashboard),
                NavItem("Inventario", Screen.Inventario.route, Icons.Default.Devices),
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
