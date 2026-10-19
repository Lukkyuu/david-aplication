package cl.duoc.prestaaula.ui.screens.cra

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import cl.duoc.prestaaula.domain.model.EstadoDevolucion
import cl.duoc.prestaaula.ui.components.ValidatedTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevolucionScreen(
    codigoInicial: String? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var codigo by remember { mutableStateOf(codigoInicial ?: "") }
    var estadoDevolucion by remember { mutableStateOf(EstadoDevolucion.BUENO) }
    var observaciones by remember { mutableStateOf("") }
    var fotoAdjunta by remember { mutableStateOf(false) }

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
                text = "Recepción de equipo del CRA",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Si el equipo se devuelve dañado o incompleto, la observación y la fotografía de evidencia son obligatorias (enviándolo a mantención).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ValidatedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = "Código Interno del Equipo (ej: NB-001)",
                leadingIcon = Icons.Default.QrCode
            )

            Text(text = "Estado al Devolver:", style = MaterialTheme.typography.labelLarge)
            androidx.compose.foundation.lazy.LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(EstadoDevolucion.values().size) { index ->
                    val est = EstadoDevolucion.values()[index]
                    FilterChip(
                        selected = estadoDevolucion == est,
                        onClick = { estadoDevolucion = est },
                        label = { Text(est.displayName) }
                    )
                }
            }

            ValidatedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = "Observaciones de recepción",
                leadingIcon = Icons.AutoMirrored.Filled.Comment,
                singleLine = false,
                maxLines = 3
            )

            if (estadoDevolucion == EstadoDevolucion.DANADO || estadoDevolucion == EstadoDevolucion.INCOMPLETO) {
                OutlinedButton(
                    onClick = { fotoAdjunta = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Tomar foto")
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(if (fotoAdjunta) "✓ Fotografía de evidencia adjunta" else "Tomar Fotografía de Evidencia (Obligatorio)")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    // Registrar devolución
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar Recepción y Actualizar Inventario")
            }
        }
    }
}
