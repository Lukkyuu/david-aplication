package cl.duoc.prestaaula.ui.screens.cra

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Warning
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
import cl.duoc.prestaaula.domain.model.EstadoDevolucion
import cl.duoc.prestaaula.ui.components.CameraCaptureButton
import cl.duoc.prestaaula.ui.components.ValidatedTextField
import cl.duoc.prestaaula.ui.viewmodel.AppViewModelFactory
import cl.duoc.prestaaula.ui.viewmodel.DevolucionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevolucionScreen(
    codigoInicial: String? = null,
    onNavigateToQrScanner: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val app = context.applicationContext as PrestaAulaApplication
    val viewModel: DevolucionViewModel = viewModel(
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
                title = { Text("Registrar Recepción / Devolución") }
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
                text = "Recepción de equipo al CRA",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Si el equipo se devuelve dañado o incompleto, la observación y la fotografía de evidencia son obligatorias, pasando a EN MANTENCIÓN.",
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
                    Text("Escanear QR con Cámara")
                }
            }

            if (uiState.prestamosActivos.isNotEmpty()) {
                Text(
                    text = "Préstamos actualmente en curso (${uiState.prestamosActivos.size}):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.prestamosActivos.forEach { p ->
                        val isSelected = uiState.prestamoSeleccionado?.prestamo?.id == p.prestamo.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.seleccionarPrestamo(p) },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
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
                                        text = "Responsable: ${p.docente.nombre} ${p.docente.apellido} (${p.prestamo.curso})",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Seleccionado",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        text = "No hay equipos con préstamos activos pendientes en este momento.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            ValidatedTextField(
                value = uiState.codigoInput,
                onValueChange = viewModel::onCodigoChanged,
                label = "Código Interno del Equipo (ej: NB-003)",
                leadingIcon = Icons.Default.QrCode
            )

            Text(text = "Estado al Devolver:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(EstadoDevolucion.values()) { est ->
                    FilterChip(
                        selected = uiState.estadoDevolucion == est,
                        onClick = { viewModel.onEstadoChanged(est) },
                        label = { Text(est.displayName) },
                        leadingIcon = if (uiState.estadoDevolucion == est) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }

            if (uiState.estadoDevolucion == EstadoDevolucion.DANADO || uiState.estadoDevolucion == EstadoDevolucion.INCOMPLETO) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Al registrar este estado, el equipo se marcará automáticamente 'EN MANTENCIÓN' y no podrá prestarse.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            ValidatedTextField(
                value = uiState.observaciones,
                onValueChange = viewModel::onObservacionesChanged,
                label = if (uiState.estadoDevolucion == EstadoDevolucion.BUENO) "Observaciones de recepción (opcional)" else "Observaciones del daño o estado (Obligatorio)",
                leadingIcon = Icons.AutoMirrored.Filled.Comment,
                errorMessage = uiState.observacionesError,
                singleLine = false,
                maxLines = 3
            )

            // Fotografía de evidencia con CameraCaptureButton
            if (uiState.estadoDevolucion == EstadoDevolucion.DANADO || uiState.estadoDevolucion == EstadoDevolucion.INCOMPLETO) {
                Column {
                    CameraCaptureButton(
                        onPhotoCaptured = { uri ->
                            viewModel.onFotoCaptured(uri.toString())
                        },
                        buttonText = if (uiState.fotoUri != null) "✓ Fotografía de evidencia adjuntada" else "Tomar Fotografía con Cámara (Obligatorio)"
                    )

                    if (uiState.fotoError != null) {
                        Text(
                            text = uiState.fotoError!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = viewModel::confirmarDevolucion,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading && uiState.prestamoSeleccionado != null
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Confirmar Recepción y Actualizar Inventario")
            }
        }
    }
}
