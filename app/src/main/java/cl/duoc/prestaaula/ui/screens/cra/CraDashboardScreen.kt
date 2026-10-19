package cl.duoc.prestaaula.ui.screens.cra

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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.prestaaula.PrestaAulaApplication
import cl.duoc.prestaaula.ui.theme.StatusDisponible
import cl.duoc.prestaaula.ui.theme.StatusMantencion
import cl.duoc.prestaaula.ui.theme.StatusPrestado
import cl.duoc.prestaaula.ui.viewmodel.AppViewModelFactory
import cl.duoc.prestaaula.ui.viewmodel.CraDashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CraDashboardScreen(
    onNavigateToInventario: () -> Unit,
    onNavigateToPrestamo: () -> Unit,
    onNavigateToDevolucion: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PrestaAulaApplication
    val viewModel: CraDashboardViewModel = viewModel(factory = AppViewModelFactory(app.container))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel CRA - Colegio Marcela Paz") }
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
                    text = "Control de Inventario y Préstamos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Gestión centralizada de recursos tecnológicos CRA",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Métricas en tiempo real desde la Base de Datos Room
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${uiState.disponiblesCount}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = StatusDisponible,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "Disponibles", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${uiState.prestadosCount}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = StatusPrestado,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "Prestados", style = MaterialTheme.typography.labelSmall)
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${uiState.mantencionCount}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = StatusMantencion,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "Mantención", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // Acciones Rápidas
            item {
                Text(
                    text = "Acciones Rápidas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onNavigateToPrestamo,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrar Entrega / Préstamo")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onNavigateToDevolucion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrar Recepción / Devolución")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onNavigateToInventario,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Devices, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver Inventario Completo (${uiState.totalEquiposCount} equipos)")
                }
            }

            // Préstamos Activos
            item {
                Text(
                    text = "Préstamos Activos en Curso (${uiState.prestamosActivos.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.prestamosActivos.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(
                            text = "No hay equipos prestados en este momento.",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                items(uiState.prestamosActivos) { p ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${p.equipo.codigoInterno} • ${p.equipo.marca} ${p.equipo.modelo}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Docente: ${p.docente.nombre} ${p.docente.apellido} • Curso: ${p.prestamo.curso}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                if (p.prestamo.observaciones.isNotBlank()) {
                                    Text(
                                        text = "Obs: ${p.prestamo.observaciones}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            OutlinedButton(onClick = onNavigateToDevolucion) {
                                Text("Devolver")
                            }
                        }
                    }
                }
            }
        }
    }
}
