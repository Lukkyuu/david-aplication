package cl.duoc.prestaaula.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.prestaaula.domain.model.EstadoEquipo
import cl.duoc.prestaaula.domain.model.EstadoReserva
import cl.duoc.prestaaula.ui.theme.StatusDeBaja
import cl.duoc.prestaaula.ui.theme.StatusDeBajaBg
import cl.duoc.prestaaula.ui.theme.StatusDisponible
import cl.duoc.prestaaula.ui.theme.StatusDisponibleBg
import cl.duoc.prestaaula.ui.theme.StatusMantencion
import cl.duoc.prestaaula.ui.theme.StatusMantencionBg
import cl.duoc.prestaaula.ui.theme.StatusPrestado
import cl.duoc.prestaaula.ui.theme.StatusPrestadoBg

@Composable
fun EquipoStatusChip(
    estado: EstadoEquipo,
    modifier: Modifier = Modifier
) {
    val targetColors = when (estado) {
        EstadoEquipo.DISPONIBLE -> Pair(StatusDisponibleBg, StatusDisponible)
        EstadoEquipo.PRESTADO -> Pair(StatusPrestadoBg, StatusPrestado)
        EstadoEquipo.EN_MANTENCION -> Pair(StatusMantencionBg, StatusMantencion)
        EstadoEquipo.DE_BAJA -> Pair(StatusDeBajaBg, StatusDeBaja)
    }

    val animatedBgColor by animateColorAsState(
        targetValue = targetColors.first,
        animationSpec = tween(durationMillis = 350),
        label = "chipBgColor"
    )
    val animatedTextColor by animateColorAsState(
        targetValue = targetColors.second,
        animationSpec = tween(durationMillis = 350),
        label = "chipTextColor"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(animatedBgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = estado.displayName,
            color = animatedTextColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun ReservaStatusChip(
    estado: EstadoReserva,
    modifier: Modifier = Modifier
) {
    val targetColors = when (estado) {
        EstadoReserva.ACTIVA -> Pair(StatusDisponibleBg, StatusDisponible)
        EstadoReserva.RETIRADA -> Pair(StatusPrestadoBg, StatusPrestado)
        EstadoReserva.CANCELADA -> Pair(StatusDeBajaBg, StatusDeBaja)
        EstadoReserva.VENCIDA -> Pair(StatusMantencionBg, StatusMantencion)
    }

    val animatedBgColor by animateColorAsState(
        targetValue = targetColors.first,
        animationSpec = tween(durationMillis = 350),
        label = "chipReservaBg"
    )
    val animatedTextColor by animateColorAsState(
        targetValue = targetColors.second,
        animationSpec = tween(durationMillis = 350),
        label = "chipReservaText"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(animatedBgColor)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = estado.displayName,
            color = animatedTextColor,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
    }
}
