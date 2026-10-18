package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.data.datastore.UserSession
import cl.duoc.prestaaula.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class SplashState {
    data object Loading : SplashState()
    data class Authenticated(val session: UserSession) : SplashState()
    data object Unauthenticated : SplashState()
}

class SplashViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _splashState = MutableStateFlow<SplashState>(SplashState.Loading)
    val splashState: StateFlow<SplashState> = _splashState.asStateFlow()

    fun checkSession() {
        viewModelScope.launch {
            val session = authRepository.currentSession.first()
            if (session.isLoggedIn && session.userId != 0L && session.rol != null) {
                _splashState.value = SplashState.Authenticated(session)
            } else {
                _splashState.value = SplashState.Unauthenticated
            }
        }
    }
}
