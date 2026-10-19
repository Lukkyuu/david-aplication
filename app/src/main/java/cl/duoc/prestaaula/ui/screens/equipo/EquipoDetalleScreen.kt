package cl.duoc.prestaaula.ui.screens.equipo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import cl.duoc.prestaaula.ui.components.EquipoStatusChip
import cl.duoc.prestaaula.ui.viewmodel.AppViewModelFactory
import cl.duoc.prestaaula.ui.viewmodel.EquipoDetalleViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipoDetalleScreen(
    equipoId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToQr: (Long) -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PrestaAulaApplication
    val viewModel: EquipoDetalleViewModel = viewModel(factory = AppViewModelFactory(app.container, equipoId))
    val uiState by viewModel.uiState.collectAsState()

    val equipo = uiState.equipo

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(equipo?.codigoInterno ?: "Detalle de Equipo") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(equipoId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar Equipo")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (equipo == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "El equipo no existe o fue eliminado.", style = MaterialTheme.typography.titleMedium)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${equipo.tipo.displayName} • ${equipo.codigoInterno}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                EquipoStatusChip(estado = equipo.estado)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "${equipo.marca} ${equipo.modelo}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (equipo.observaciones.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Obs: ${equipo.observaciones}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onNavigateToQr(equipoId) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.QrCode, contentDescription = "Ver Código QR")
                                Spacer(modifier = Modifier.padding(4.dp))
                                Text("Ver Código QR del Equipo")
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
                            Text(
                                text = "Último Docente Responsable",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            val ultimo = uiState.ultimoResponsable
                            if (ultimo == null) {
                                Text(text = "Sin registros previos de préstamo para este equipo.")
                            } else {
                                Text(text = "Docente: ${ultimo.docente.nombre} ${ultimo.docente.apellido}")
                                Text(text = "Curso: ${ultimo.prestamo.curso}")
                                val fechaRetiroStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(ultimo.prestamo.fechaHoraRetiro))
                                Text(text = "Fecha Retiro: $fechaRetiroStr")
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "Historial Completo de Préstamos (${uiState.historial.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(uiState.historial) { prestamoConDetalle ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Docente: ${prestamoConDetalle.docente.nombre} ${prestamoConDetalle.docente.apellido}",
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(text = "Curso: ${prestamoConDetalle.prestamo.curso}")
                            val retiro = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(prestamoConDetalle.prestamo.fechaHoraRetiro))
                            Text(text = "Retiro: $retiro")
                            val devTime = prestamoConDetalle.prestamo.fechaHoraDevolucion
                            if (devTime != null) {
                                val devolucion = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(devTime))
                                Text(text = "Devolución: $devolucion • Estado: ${prestamoConDetalle.prestamo.estadoAlDevolver?.displayName ?: "OK"}")
                            } else {
                                Text(text = "Estado: En Préstamo Activo", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
