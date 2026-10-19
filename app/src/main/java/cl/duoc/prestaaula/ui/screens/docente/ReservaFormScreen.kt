package cl.duoc.prestaaula.ui.screens.docente

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.prestaaula.PrestaAulaApplication
import cl.duoc.prestaaula.ui.components.ValidatedTextField
import cl.duoc.prestaaula.ui.viewmodel.AppViewModelFactory
import cl.duoc.prestaaula.ui.viewmodel.ReservaFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaFormScreen(
    equipoId: Long,
    onNavigateBack: () -> Unit,
    onReservaSuccess: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PrestaAulaApplication
    val viewModel: ReservaFormViewModel = viewModel(factory = AppViewModelFactory(app.container, equipoId))
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            snackbarHostState.showSnackbar("¡Reserva realizada con éxito!")
            onReservaSuccess()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Reserva de Recurso") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Formulario de Solicitud CRA",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Máximo 2 reservas activas por bloque. No se admiten reservas pasadas.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Equipo seleccionado o información
            Text(text = "Equipo Seleccionado:", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(4.dp))
            val equipoInfo = uiState.equipo?.let { "${it.tipo.displayName} - ${it.codigoInterno} (${it.marca} ${it.modelo})" } ?: "Ningún equipo específico seleccionado"
            ValidatedTextField(
                value = equipoInfo,
                onValueChange = {},
                label = "Recurso CRA",
                leadingIcon = Icons.Default.Devices,
                readOnly = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            ValidatedTextField(
                value = uiState.fecha,
                onValueChange = viewModel::onFechaChanged,
                label = "Fecha (YYYY-MM-DD)",
                leadingIcon = Icons.Default.CalendarToday
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Bloque Horario:", style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(4.dp))
            androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
                items(uiState.bloques.size) { index ->
                    val bloque = uiState.bloques[index]
                    FilterChip(
                        selected = uiState.bloqueId == bloque.id,
                        onClick = { viewModel.onBloqueChanged(bloque.id) },
                        label = { Text("B${bloque.numeroBloque} (${bloque.horaInicio})") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ValidatedTextField(
                value = uiState.curso,
                onValueChange = viewModel::onCursoChanged,
                label = "Curso o Asignatura (ej: 3° Medio A)",
                leadingIcon = Icons.Default.Class
            )

            Spacer(modifier = Modifier.height(12.dp))

            ValidatedTextField(
                value = uiState.observaciones,
                onValueChange = viewModel::onObservacionesChanged,
                label = "Observaciones (opcional)",
                leadingIcon = Icons.AutoMirrored.Filled.Comment,
                singleLine = false,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewModel::confirmarReserva,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Confirmar Reserva")
            }
        }
    }
}
