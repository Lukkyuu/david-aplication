package cl.duoc.prestaaula.ui.screens.reportes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.prestaaula.PrestaAulaApplication
import cl.duoc.prestaaula.ui.theme.Amber40
import cl.duoc.prestaaula.ui.theme.Navy40
import cl.duoc.prestaaula.ui.theme.Navy80
import cl.duoc.prestaaula.ui.theme.StatusDeBaja
import cl.duoc.prestaaula.ui.theme.StatusDisponible
import cl.duoc.prestaaula.ui.theme.StatusMantencion
import cl.duoc.prestaaula.ui.theme.StatusPrestado
import cl.duoc.prestaaula.ui.viewmodel.AppViewModelFactory
import cl.duoc.prestaaula.ui.viewmodel.ReportesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportesScreen() {
    val context = LocalContext.current
    val app = context.applicationContext as PrestaAulaApplication
    val viewModel: ReportesViewModel = viewModel(factory = AppViewModelFactory(app.container))
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes UTP & Finanzas • CRA") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Indicadores Institucionales",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Colegio Marcela Paz • Reporte de trazabilidad tecnológica",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Métricas principales
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "Total Equipos", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${uiState.totalEquipos}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "Préstamos Activos", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${uiState.prestadosCount}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = StatusPrestado
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(text = "En Mantención", style = MaterialTheme.typography.bodySmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${uiState.mantencionCount}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = StatusMantencion
                            )
                        }
                    }
                }
            }

            // Gráfico de Barras con Compose Canvas
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Equipos Más Solicitados (Canvas Bar Chart)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))

                        val equiposStats = uiState.equiposMasPrestados
                        if (equiposStats.isEmpty()) {
                            Text(
                                text = "Sin datos de uso suficientes para graficar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            val maxUsos = (equiposStats.maxOfOrNull { it.totalPrestamos } ?: 1).coerceAtLeast(1)

                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                equiposStats.forEach { stat ->
                                    val ratio = stat.totalPrestamos.toFloat() / maxUsos.toFloat()
                                    Column {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${stat.codigoInterno} (${stat.marca} ${stat.modelo})",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Text(
                                                text = "${stat.totalPrestamos} préstamos",
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Navy40
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Canvas(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(14.dp)
                                        ) {
                                            // Fondo de barra
                                            drawRoundRect(
                                                color = Color(0xFFE2E8F0),
                                                size = size,
                                                cornerRadius = CornerRadius(8f, 8f)
                                            )
                                            // Barra con valor proporcional
                                            drawRoundRect(
                                                color = Navy40,
                                                size = Size(width = size.width * ratio, height = size.height),
                                                cornerRadius = CornerRadius(8f, 8f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Desglose de Estado del Inventario
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PieChart, contentDescription = null, tint = Amber40)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Distribución del Inventario CRA",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "• Disponibles para préstamo:")
                            Text(text = "${uiState.disponiblesCount} equipos", fontWeight = FontWeight.Bold, color = StatusDisponible)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "• En uso en salas:")
                            Text(text = "${uiState.prestadosCount} equipos", fontWeight = FontWeight.Bold, color = StatusPrestado)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "• En taller / mantención:")
                            Text(text = "${uiState.mantencionCount} equipos", fontWeight = FontWeight.Bold, color = StatusMantencion)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "• Dados de baja:")
                            Text(text = "${uiState.deBajaCount} equipos", fontWeight = FontWeight.Bold, color = StatusDeBaja)
                        }
                    }
                }
            }

            // Docentes con Incidencias
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Registro de Incidencias y Daños",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        if (uiState.docentesConIncidencias.isEmpty()) {
                            Text(
                                text = "No se registran devoluciones con daño o incompletas.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            uiState.docentesConIncidencias.forEach { inc ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${inc.nombre} ${inc.apellido} (${inc.email})",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = "${inc.totalIncidentes} incidencia(s)",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Historial Reciente de Transacciones
            item {
                Text(
                    text = "Últimas Transacciones Registradas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(uiState.prestamosRecientes) { item ->
                val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(item.prestamo.fechaHoraRetiro))
                val devuelto = item.prestamo.fechaHoraDevolucion != null
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                                text = "${item.equipo.codigoInterno} • ${item.docente.nombre} ${item.docente.apellido}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Curso: ${item.prestamo.curso} • Fecha: $dateStr",
                                style = MaterialTheme.typography.labelSmall
                            )
                            if (item.prestamo.observaciones.isNotBlank()) {
                                Text(
                                    text = "Obs: ${item.prestamo.observaciones}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = if (devuelto) "Devuelto" else "En curso",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (devuelto) StatusDisponible else StatusPrestado
                        )
                    }
                }
            }
        }
    }
}
