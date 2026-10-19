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
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
fun PrestamoScreen(
    codigoInicial: String? = null
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var codigo by remember { mutableStateOf(codigoInicial ?: "") }
    var docenteRut by remember { mutableStateOf("") }
    var curso by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Entrega / Préstamo") }
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
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Escanee o ingrese el código interno del equipo y el RUT del docente solicitante.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ValidatedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = "Código Interno del Equipo (ej: NB-001)",
                leadingIcon = Icons.Default.QrCode
            )

            ValidatedTextField(
                value = docenteRut,
                onValueChange = { docenteRut = it },
                label = "RUT del Docente Responsable",
                leadingIcon = Icons.Default.Person
            )

            ValidatedTextField(
                value = curso,
                onValueChange = { curso = it },
                label = "Curso o Asignatura (ej: 2° Medio A)",
                leadingIcon = Icons.Default.Class
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    // Acción simulada o conectada a registrarPrestamoUseCase
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar Préstamo y Asignar Equipo")
            }
        }
    }
}
