package cl.duoc.prestaaula.ui.screens.perfil

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.prestaaula.PrestaAulaApplication
import cl.duoc.prestaaula.domain.model.RolUsuario
import cl.duoc.prestaaula.ui.viewmodel.AppViewModelFactory
import cl.duoc.prestaaula.ui.viewmodel.PerfilViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    currentRole: RolUsuario?,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PrestaAulaApplication
    val viewModel: PerfilViewModel = viewModel(factory = AppViewModelFactory(app.container))
    val session by viewModel.currentSession.collectAsState()

    val nombreCompleto = if (session.nombre.isNotBlank()) "${session.nombre} ${session.apellido}" else "Usuario Conectado"
    val emailText = session.email.ifBlank { "Sin correo registrado" }
    val rutText = session.rut.ifBlank { "N/A" }
    val rolText = session.rol?.displayName ?: currentRole?.displayName ?: "Sin Rol"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar de usuario",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(96.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = nombreCompleto,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = emailText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Datos de Cuenta",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "RUT: $rutText")
                    Text(text = "Rol Activo: $rolText")
                    Text(text = "Institución: Colegio Marcela Paz")
                    Text(text = "Sede: CRA Central y Sala Tecnológica")
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = {
                    viewModel.logout(onLogout)
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar Sesión")
            }
        }
    }
}
