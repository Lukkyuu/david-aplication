package cl.duoc.prestaaula.ui.screens.cra

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import cl.duoc.prestaaula.ui.components.ValidatedTextField
import cl.duoc.prestaaula.ui.viewmodel.AppViewModelFactory
import cl.duoc.prestaaula.ui.viewmodel.PrestamoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrestamoScreen(
    codigoInicial: String? = null,
    onNavigateToQrScanner: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val app = context.applicationContext as PrestaAulaApplication
    val viewModel: PrestamoViewModel = viewModel(
        factory = AppViewModelFactory(app.container, extraArgString = codigoInicial)
    )
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { success ->
            snackbarHostState.showSnackbar(success)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Entrega / Préstamo CRA") }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Entrega de recurso pedagógico",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Escanee o ingrese el código del equipo disponible y asigne al docente responsable.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (onNavigateToQrScanner != null) {
                Button(
                    onClick = onNavigateToQrScanner,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.QrCode, contentDescription = "Escanear QR")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Escanear Código QR con Cámara")
                }
            }

            if (uiState.reservasHoy.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Reservas para Hoy (${uiState.reservasHoy.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Toque 'Entregar' para registrar el préstamo en 1 toque:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        uiState.reservasHoy.forEach { r ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${r.equipo.codigoInterno} (${r.equipo.modelo})",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "Prof. ${r.docente.nombre} ${r.docente.apellido} • ${r.reserva.curso} • ${r.bloque.etiqueta}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Button(
                                    onClick = { viewModel.entregarReserva(r) },
                                    enabled = !uiState.isLoading
                                ) {
                                    Text("Entregar")
                                }
                            }
                        }
                    }
                }
            }

            ValidatedTextField(
                value = uiState.codigoEquipo,
                onValueChange = viewModel::onCodigoChanged,
                label = "Código Interno del Equipo (ej: NB-001)",
                placeholder = "NB-001, PROY-002, TAB-003...",
                leadingIcon = Icons.Default.QrCode
            )

            if (uiState.equiposDisponibles.isNotEmpty()) {
                Text(text = "Equipos disponibles actualmente:", style = MaterialTheme.typography.labelSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(uiState.equiposDisponibles) { eq ->
                        FilterChip(
                            selected = uiState.equipoSeleccionado?.id == eq.id,
                            onClick = { viewModel.onEquipoSelected(eq) },
                            label = { Text("${eq.codigoInterno} (${eq.tipo.displayName})") },
                            leadingIcon = if (uiState.equipoSeleccionado?.id == eq.id) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }
            }

            ValidatedTextField(
                value = uiState.docenteRut,
                onValueChange = viewModel::onDocenteRutChanged,
                label = "RUT del Docente (ej: 14.567.890-2)",
                placeholder = "14.567.890-2",
                leadingIcon = Icons.Default.Person
            )

            ValidatedTextField(
                value = uiState.curso,
                onValueChange = viewModel::onCursoChanged,
                label = "Curso o Asignatura",
                placeholder = "ej: 2° Medio A, Laboratorio de Ciencias",
                leadingIcon = Icons.Default.Class,
                errorMessage = uiState.cursoError,
                isValid = uiState.curso.isNotBlank()
            )

            ValidatedTextField(
                value = uiState.observaciones,
                onValueChange = viewModel::onObservacionesChanged,
                label = "Observaciones de entrega (opcional)",
                placeholder = "ej: Se entrega con cargador y bolso",
                leadingIcon = Icons.AutoMirrored.Filled.Comment,
                singleLine = false,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = viewModel::confirmarPrestamo,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading && uiState.codigoEquipo.isNotBlank() && uiState.curso.isNotBlank()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Confirmar Préstamo y Asignar Equipo")
            }
        }
    }
}
