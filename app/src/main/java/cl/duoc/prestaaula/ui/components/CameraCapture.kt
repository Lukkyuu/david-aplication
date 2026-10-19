package cl.duoc.prestaaula.ui.components

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CameraCaptureButton(
    onPhotoCaptured: (Uri) -> Unit,
    modifier: Modifier = Modifier,
    buttonText: String = "Tomar Fotografía de Evidencia"
) {
    val context = LocalContext.current
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var showPermissionRationale by remember { mutableStateOf(false) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoUri?.let { uri ->
                onPhotoCaptured(uri)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageUri(context)
            photoUri = uri
            takePictureLauncher.launch(uri)
        } else {
            showPermissionRationale = true
        }
    }

    OutlinedButton(
        onClick = {
            val permission = Manifest.permission.CAMERA
            when {
                context.checkSelfPermission(permission) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                    val uri = createImageUri(context)
                    photoUri = uri
                    takePictureLauncher.launch(uri)
                }
                else -> {
                    permissionLauncher.launch(permission)
                }
            }
        },
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.CameraAlt, contentDescription = "Cámara")
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(4.dp))
        Text(buttonText)
    }

    if (showPermissionRationale) {
        AlertDialog(
            onDismissRequest = { showPermissionRationale = false },
            title = { Text("Permiso de Cámara Requerido") },
            text = { Text("PrestaAula necesita acceso a la cámara para registrar fotografías de evidencia en devoluciones con daños o incidencias.") },
            confirmButton = {
                TextButton(onClick = {
                    showPermissionRationale = false
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text("Conceder Permiso")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionRationale = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun createImageUri(context: Context): Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir = File(context.cacheDir, "images").apply { mkdirs() }
    val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
    val authority = "${context.packageName}.fileprovider"
    return FileProvider.getUriForFile(context, authority, imageFile)
}
