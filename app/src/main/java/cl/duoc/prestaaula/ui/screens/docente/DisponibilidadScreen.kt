package cl.duoc.prestaaula.ui.screens.docente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import cl.duoc.prestaaula.domain.model.TipoEquipo
import cl.duoc.prestaaula.ui.components.EquipoStatusChip
import cl.duoc.prestaaula.ui.viewmodel.AppViewModelFactory
import cl.duoc.prestaaula.ui.viewmodel.DisponibilidadViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisponibilidadScreen(
    onNavigateToReserva: (Long) -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PrestaAulaApplication
    val viewModel: DisponibilidadViewModel = viewModel(factory = AppViewModelFactory(app.container))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Disponibilidad de Equipos CRA") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = uiState.fechaSeleccionada,
                onValueChange = viewModel::onFechaChanged,
                label = { Text("Fecha (YYYY-MM-DD)") },
                leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Fecha") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(text = "Tipo de Recurso:", style = MaterialTheme.typography.labelMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = uiState.tipoFiltro == null,
                        onClick = { viewModel.onTipoFiltroChanged(null) },
                        label = { Text("Todos") }
                    )
                }
                items(TipoEquipo.values()) { tipo ->
                    FilterChip(
                        selected = uiState.tipoFiltro == tipo,
                        onClick = { viewModel.onTipoFiltroChanged(tipo) },
                        label = { Text(tipo.displayName) }
                    )
                }
            }

            Text(text = "Bloque Horario:", style = MaterialTheme.typography.labelMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.bloques) { bloque ->
                    FilterChip(
                        selected = uiState.bloqueSeleccionadoId == bloque.id,
                        onClick = { viewModel.onBloqueChanged(bloque.id) },
                        label = { Text("B${bloque.numeroBloque} (${bloque.horaInicio})") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Equipos Disponibles (${uiState.equiposDisponibles.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(uiState.equiposDisponibles) { equipo ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${equipo.tipo.displayName} • ${equipo.codigoInterno}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${equipo.marca} ${equipo.modelo}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                EquipoStatusChip(estado = equipo.estado)
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { onNavigateToReserva(equipo.id) }) {
                                    Text("Reservar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
