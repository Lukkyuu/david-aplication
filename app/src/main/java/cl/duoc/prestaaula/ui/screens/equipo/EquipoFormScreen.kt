package cl.duoc.prestaaula.ui.screens.equipo

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
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.QrCode
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
fun EquipoFormScreen(
    equipoId: Long? = null,
    onNavigateBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val isEdit = equipoId != null && equipoId > 0
    val snackbarHostState = remember { SnackbarHostState() }

    var codigoInterno by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Editar Equipo" else "Registrar Equipo") },
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
                text = "Ficha de Equipo Tecnológico",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "El código interno debe ser único en el inventario del colegio (ej: NB-001, PROY-002).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            ValidatedTextField(
                value = codigoInterno,
                onValueChange = { codigoInterno = it },
                label = "Código Interno (ej: NB-001)",
                leadingIcon = Icons.Default.QrCode
            )

            Spacer(modifier = Modifier.height(12.dp))

            ValidatedTextField(
                value = marca,
                onValueChange = { marca = it },
                label = "Marca (ej: Lenovo, Epson, Apple)",
                leadingIcon = Icons.Default.Devices
            )

            Spacer(modifier = Modifier.height(12.dp))

            ValidatedTextField(
                value = modelo,
                onValueChange = { modelo = it },
                label = "Modelo (ej: ThinkPad E14, PowerLite X49)",
                leadingIcon = Icons.Default.Category
            )

            Spacer(modifier = Modifier.height(12.dp))

            ValidatedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                label = "Observaciones iniciales",
                leadingIcon = Icons.AutoMirrored.Filled.Comment,
                singleLine = false,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onSaveSuccess,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEdit) "Guardar Cambios" else "Registrar en Inventario")
            }
        }
    }
}
