package cl.duoc.prestaaula.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.prestaaula.PrestaAulaApplication
import cl.duoc.prestaaula.domain.model.RolUsuario
import cl.duoc.prestaaula.ui.viewmodel.AppViewModelFactory
import cl.duoc.prestaaula.ui.viewmodel.SplashState
import cl.duoc.prestaaula.ui.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: (RolUsuario) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val app = context.applicationContext as PrestaAulaApplication
    val viewModel: SplashViewModel = viewModel(factory = AppViewModelFactory(app.container))
    val splashState by viewModel.splashState.collectAsState()

    val scale = remember { Animatable(0.6f) }

    LaunchedEffect(Unit) {
        viewModel.checkSession()
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(
                durationMillis = 800,
                easing = FastOutSlowInEasing
            )
        )
    }

    LaunchedEffect(splashState) {
        when (val state = splashState) {
            is SplashState.Authenticated -> {
                delay(400)
                state.session.rol?.let { role ->
                    onNavigateToHome(role)
                } ?: onNavigateToLogin()
            }
            is SplashState.Unauthenticated -> {
                delay(400)
                onNavigateToLogin()
            }
            is SplashState.Loading -> { }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale.value)
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = "Logo PrestaAula",
                tint = Color.White,
                modifier = Modifier.size(96.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "PrestaAula",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 32.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Colegio Marcela Paz • CRA & Tecnología",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.85f)
                )
            )
        }
    }
}
