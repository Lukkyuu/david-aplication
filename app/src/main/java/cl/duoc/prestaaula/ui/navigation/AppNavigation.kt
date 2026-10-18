package cl.duoc.prestaaula.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cl.duoc.prestaaula.domain.model.RolUsuario
import cl.duoc.prestaaula.ui.screens.auth.LoginScreen
import cl.duoc.prestaaula.ui.screens.auth.RegisterScreen
import cl.duoc.prestaaula.ui.screens.cra.CraDashboardScreen
import cl.duoc.prestaaula.ui.screens.cra.DevolucionScreen
import cl.duoc.prestaaula.ui.screens.cra.InventarioScreen
import cl.duoc.prestaaula.ui.screens.cra.PrestamoScreen
import cl.duoc.prestaaula.ui.screens.docente.DisponibilidadScreen
import cl.duoc.prestaaula.ui.screens.docente.DocenteHomeScreen
import cl.duoc.prestaaula.ui.screens.docente.HorarioDocenteScreen
import cl.duoc.prestaaula.ui.screens.docente.MisReservasScreen
import cl.duoc.prestaaula.ui.screens.docente.ReservaFormScreen
import cl.duoc.prestaaula.ui.screens.equipo.EquipoDetalleScreen
import cl.duoc.prestaaula.ui.screens.equipo.EquipoFormScreen
import cl.duoc.prestaaula.ui.screens.equipo.EquipoQrScreen
import cl.duoc.prestaaula.ui.screens.perfil.PerfilScreen
import cl.duoc.prestaaula.ui.screens.reportes.ReportesScreen
import cl.duoc.prestaaula.ui.screens.splash.SplashScreen

@Composable
fun PrestaAulaAppNavigation() {
    val navController = rememberNavController()
    var currentRole by remember { mutableStateOf<RolUsuario?>(null) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    val navItems = RoleNavItems.getItemsForRole(currentRole)
    val shouldShowBottomBar = currentRole != null && navItems.any { it.route == currentDestination }

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                NavigationBar {
                    navItems.forEach { item ->
                        val isSelected = currentDestination == item.route
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            // Splash
            composable(Screen.Splash.route) {
                SplashScreen(
                    onTimeout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            // Auth
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { role ->
                        currentRole = role
                        val destination = when (role) {
                            RolUsuario.DOCENTE -> Screen.DocenteHome.route
                            RolUsuario.ENCARGADO_CRA -> Screen.CraDashboard.route
                            RolUsuario.UTP, RolUsuario.ADMIN_FINANZAS -> Screen.Reportes.route
                        }
                        navController.navigate(destination) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onRegisterSuccess = {
                        navController.popBackStack()
                    }
                )
            }

            // Perfil
            composable(Screen.Perfil.route) {
                PerfilScreen(
                    currentRole = currentRole,
                    onLogout = {
                        currentRole = null
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // Docente
            composable(Screen.DocenteHome.route) {
                DocenteHomeScreen(
                    onNavigateToDisponibilidad = { navController.navigate(Screen.Disponibilidad.route) },
                    onNavigateToReservaForm = { navController.navigate(Screen.ReservaForm.createRoute()) }
                )
            }

            composable(Screen.Disponibilidad.route) {
                DisponibilidadScreen(
                    onNavigateToReserva = { equipoId ->
                        navController.navigate(Screen.ReservaForm.createRoute(equipoId))
                    }
                )
            }

            composable(
                route = Screen.ReservaForm.route,
                arguments = listOf(navArgument("equipoId") { type = NavType.LongType; defaultValue = 0L })
            ) { backStackEntry ->
                val equipoId = backStackEntry.arguments?.getLong("equipoId") ?: 0L
                ReservaFormScreen(
                    equipoId = equipoId,
                    onNavigateBack = { navController.popBackStack() },
                    onReservaSuccess = { navController.popBackStack() }
                )
            }

            composable(Screen.MisReservas.route) {
                MisReservasScreen()
            }

            composable(Screen.HorarioDocente.route) {
                HorarioDocenteScreen()
            }

            // CRA
            composable(Screen.CraDashboard.route) {
                CraDashboardScreen(
                    onNavigateToInventario = { navController.navigate(Screen.Inventario.route) },
                    onNavigateToPrestamo = { navController.navigate(Screen.Prestamo.createRoute()) },
                    onNavigateToDevolucion = { navController.navigate(Screen.Devolucion.createRoute()) }
                )
            }

            composable(Screen.Inventario.route) {
                InventarioScreen(
                    onNavigateToNuevoEquipo = { navController.navigate(Screen.EquipoForm.createRoute()) },
                    onNavigateToDetalle = { equipoId ->
                        navController.navigate(Screen.EquipoDetalle.createRoute(equipoId))
                    }
                )
            }

            composable(
                route = Screen.EquipoForm.route,
                arguments = listOf(navArgument("equipoId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val equipoIdStr = backStackEntry.arguments?.getString("equipoId")
                val equipoId = equipoIdStr?.toLongOrNull()
                EquipoFormScreen(
                    equipoId = equipoId,
                    onNavigateBack = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.EquipoDetalle.route,
                arguments = listOf(navArgument("equipoId") { type = NavType.LongType })
            ) { backStackEntry ->
                val equipoId = backStackEntry.arguments?.getLong("equipoId") ?: 0L
                EquipoDetalleScreen(
                    equipoId = equipoId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id -> navController.navigate(Screen.EquipoForm.createRoute(id)) },
                    onNavigateToQr = { id -> navController.navigate(Screen.EquipoQr.createRoute(id)) }
                )
            }

            composable(
                route = Screen.EquipoQr.route,
                arguments = listOf(navArgument("equipoId") { type = NavType.LongType })
            ) { backStackEntry ->
                val equipoId = backStackEntry.arguments?.getLong("equipoId") ?: 0L
                EquipoQrScreen(
                    equipoId = equipoId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Screen.Prestamo.route,
                arguments = listOf(navArgument("codigo") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val codigo = backStackEntry.arguments?.getString("codigo")
                PrestamoScreen(codigoInicial = codigo)
            }

            composable(
                route = Screen.Devolucion.route,
                arguments = listOf(navArgument("codigo") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                })
            ) { backStackEntry ->
                val codigo = backStackEntry.arguments?.getString("codigo")
                DevolucionScreen(codigoInicial = codigo)
            }

            // Reportes
            composable(Screen.Reportes.route) {
                ReportesScreen()
            }
        }
    }
}
