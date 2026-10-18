package cl.duoc.prestaaula.ui.screens.cra

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevolucionScreen(
    codigoInicial: String? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Recepción / Devolución") }
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
            Text(
                text = "Recepción de equipo devuelto",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "1. Escanear QR del equipo -> 2. Indicar estado (Bueno, Observaciones, Dañado, Incompleto) con foto obligatoria si viene con daño -> Confirmar.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
