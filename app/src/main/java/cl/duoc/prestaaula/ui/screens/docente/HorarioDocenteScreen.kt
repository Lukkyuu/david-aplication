package cl.duoc.prestaaula.ui.screens.docente

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.prestaaula.PrestaAulaApplication
import cl.duoc.prestaaula.domain.model.DiaSemana
import cl.duoc.prestaaula.ui.viewmodel.AppViewModelFactory
import cl.duoc.prestaaula.ui.viewmodel.HorarioDocenteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HorarioDocenteScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as PrestaAulaApplication
    val viewModel: HorarioDocenteViewModel = viewModel(factory = AppViewModelFactory(app.container))
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.mensaje) {
        uiState.mensaje?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMensaje()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Horario de Clases Semanal") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                    text = "Disponibilidad semanal del Docente",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Marca los bloques pedagógicos en los que tienes clase para optimizar la sugerencia de reservas en el CRA.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Selector de día
            item {
                Text(text = "Seleccione el día de la semana:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val diasHabiles = listOf(
                        DiaSemana.LUNES,
                        DiaSemana.MARTES,
                        DiaSemana.MIERCOLES,
                        DiaSemana.JUEVES,
                        DiaSemana.VIERNES
                    )
                    items(diasHabiles) { dia ->
                        FilterChip(
                            selected = uiState.diaSeleccionado == dia,
                            onClick = { viewModel.seleccionarDia(dia) },
                            label = { Text(dia.displayName) },
                            leadingIcon = if (uiState.diaSeleccionado == dia) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }
            }

            // Bloques para el día seleccionado
            item {
                val dia = uiState.diaSeleccionado
                val bloquesActivos = uiState.bloquesDocentePorDia[dia] ?: emptySet()
                Text(
                    text = "Bloques pedagógicos para ${dia.displayName} (${bloquesActivos.size} asignados):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            val diaActual = uiState.diaSeleccionado
            val bloquesActivosActual = uiState.bloquesDocentePorDia[diaActual] ?: emptySet()

            items(uiState.bloques) { bloque ->
                val isChecked = bloquesActivosActual.contains(bloque.id)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleBloque(diaActual, bloque.id) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isChecked) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isChecked) 2.dp else 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { viewModel.toggleBloque(diaActual, bloque.id) }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = bloque.etiqueta,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Horario: ${bloque.horaInicio} - ${bloque.horaFin}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (isChecked) {
                            Icon(
                                Icons.Default.Schedule,
                                contentDescription = "Asignado",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Botón Guardar
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = viewModel::guardarHorario,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Guardar Mi Horario Semanal")
                }
            }
        }
    }
}
