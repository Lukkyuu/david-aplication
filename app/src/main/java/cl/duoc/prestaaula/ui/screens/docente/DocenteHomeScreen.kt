package cl.duoc.prestaaula.ui.screens.docente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.prestaaula.PrestaAulaApplication
import cl.duoc.prestaaula.ui.viewmodel.AppViewModelFactory
import cl.duoc.prestaaula.ui.viewmodel.DocenteHomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocenteHomeScreen(
    onNavigateToDisponibilidad: () -> Unit,
    onNavigateToReservaForm: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PrestaAulaApplication
    val viewModel: DocenteHomeViewModel = viewModel(factory = AppViewModelFactory(app.container))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Docente • ${uiState.docenteNombre}") }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToReservaForm,
                icon = { Icon(Icons.Default.Add, contentDescription = "Nueva Reserva") },
                text = { Text("Nueva Reserva") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "¡Bienvenido/a, ${uiState.docenteNombre}!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Consulta la disponibilidad y gestiona tus recursos pedagógicos CRA",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                OutlinedButton(
                    onClick = onNavigateToDisponibilidad,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Devices, contentDescription = "Disponibilidad")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Consultar Disponibilidad de Equipos")
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row {
                            Icon(Icons.Default.Bookmark, contentDescription = "Reservas hoy")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mis Reservas para Hoy (${uiState.reservasHoy.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (uiState.reservasHoy.isEmpty()) {
                            Text(
                                text = "No tienes reservas activas para el día de hoy.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            uiState.reservasHoy.forEach { reserva ->
                                Text(
                                    text = "• ${reserva.equipo.tipo.displayName} (${reserva.equipo.codigoInterno}) - Curso: ${reserva.reserva.curso}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row {
                            Icon(Icons.Default.Devices, contentDescription = "Equipos a cargo")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Préstamos Activos a mi Cargo (${uiState.prestamosActivos.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (uiState.prestamosActivos.isEmpty()) {
                            Text(
                                text = "No tienes equipos pendientes de devolución actualmente.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            uiState.prestamosActivos.forEach { prestamo ->
                                Text(
                                    text = "• ${prestamo.equipo.tipo.displayName} (${prestamo.equipo.codigoInterno}) - Curso: ${prestamo.prestamo.curso}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
