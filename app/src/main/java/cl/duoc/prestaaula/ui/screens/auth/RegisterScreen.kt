package cl.duoc.prestaaula.ui.screens.auth

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.prestaaula.PrestaAulaApplication
import cl.duoc.prestaaula.domain.model.RolUsuario
import cl.duoc.prestaaula.ui.components.ValidatedTextField
import cl.duoc.prestaaula.ui.viewmodel.AppViewModelFactory
import cl.duoc.prestaaula.ui.viewmodel.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PrestaAulaApplication
    val viewModel: RegisterViewModel = viewModel(factory = AppViewModelFactory(app.container))
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onRegisterSuccess()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Usuario") },
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
                text = "Crear cuenta institucional",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Personal docente y administrativo del Colegio Marcela Paz.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            ValidatedTextField(
                value = uiState.rut,
                onValueChange = viewModel::onRutChanged,
                label = "RUT (ej: 12.345.678-9)",
                leadingIcon = Icons.Default.Badge,
                errorMessage = uiState.rutError,
                isValid = uiState.rut.isNotBlank() && uiState.rutError == null
            )

            Spacer(modifier = Modifier.height(12.dp))

            ValidatedTextField(
                value = uiState.nombre,
                onValueChange = viewModel::onNombreChanged,
                label = "Nombre",
                leadingIcon = Icons.Default.Person,
                errorMessage = uiState.nombreError,
                isValid = uiState.nombre.isNotBlank() && uiState.nombreError == null
            )

            Spacer(modifier = Modifier.height(12.dp))

            ValidatedTextField(
                value = uiState.apellido,
                onValueChange = viewModel::onApellidoChanged,
                label = "Apellido",
                leadingIcon = Icons.Default.Person,
                errorMessage = uiState.apellidoError,
                isValid = uiState.apellido.isNotBlank() && uiState.apellidoError == null
            )

            Spacer(modifier = Modifier.height(12.dp))

            ValidatedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChanged,
                label = "Email Institucional (@marcelapaz.cl)",
                placeholder = "usuario@marcelapaz.cl",
                leadingIcon = Icons.Default.Email,
                errorMessage = uiState.emailError,
                isValid = uiState.email.isNotBlank() && uiState.emailError == null,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Rol Institucional:",
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilterChip(
                    selected = uiState.rol == RolUsuario.DOCENTE,
                    onClick = { viewModel.onRolChanged(RolUsuario.DOCENTE) },
                    label = { Text("Docente") }
                )
                FilterChip(
                    selected = uiState.rol == RolUsuario.ENCARGADO_CRA,
                    onClick = { viewModel.onRolChanged(RolUsuario.ENCARGADO_CRA) },
                    label = { Text("Encargado CRA") }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            ValidatedTextField(
                value = uiState.password,
                onValueChange = viewModel::onPasswordChanged,
                label = "Contraseña (min 8 chars, M, 123, !@#)",
                leadingIcon = Icons.Default.Lock,
                errorMessage = uiState.passwordError,
                isValid = uiState.password.isNotBlank() && uiState.passwordError == null,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingCustomIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ValidatedTextField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChanged,
                label = "Confirmar Contraseña",
                leadingIcon = Icons.Default.Lock,
                errorMessage = uiState.confirmPasswordError,
                isValid = uiState.confirmPassword.isNotBlank() && uiState.confirmPasswordError == null,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewModel::register,
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
                Text("Registrar Usuario")
            }
        }
    }
}
