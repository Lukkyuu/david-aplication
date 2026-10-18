package cl.duoc.prestaaula.ui.screens.docente

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.prestaaula.ui.components.ValidatedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaFormScreen(
    equipoId: Long,
    onNavigateBack: () -> Unit,
    onReservaSuccess: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var fecha by remember { mutableStateOf("") }
    var bloque by remember { mutableStateOf("") }
    var curso by remember { mutableStateOf("") }
    var observacion by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Reserva") },
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
                text = "Reserva de recurso pedagógico",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Recuerda que no se permiten dos reservas activas para el mismo equipo en un mismo bloque.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            ValidatedTextField(
                value = fecha,
                onValueChange = { fecha = it },
                label = "Fecha (YYYY-MM-DD)",
                leadingIcon = Icons.Default.CalendarToday
            )

            Spacer(modifier = Modifier.height(12.dp))

            ValidatedTextField(
                value = bloque,
                onValueChange = { bloque = it },
                label = "Bloque Horario",
                leadingIcon = Icons.Default.Schedule
            )

            Spacer(modifier = Modifier.height(12.dp))

            ValidatedTextField(
                value = curso,
                onValueChange = { curso = it },
                label = "Curso (ej: 3°B o 1° Medio A)",
                leadingIcon = Icons.Default.Class
            )

            Spacer(modifier = Modifier.height(12.dp))

            ValidatedTextField(
                value = observacion,
                onValueChange = { observacion = it },
                label = "Observaciones (opcional)",
                leadingIcon = Icons.Default.Comment,
                singleLine = false,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onReservaSuccess,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar Reserva")
            }
        }
    }
}
