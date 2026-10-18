package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.domain.model.RolUsuario
import cl.duoc.prestaaula.domain.repository.AuthRepository
import cl.duoc.prestaaula.domain.validation.EmailValidator
import cl.duoc.prestaaula.domain.validation.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val emailError: String? = null,
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loggedInRole: RolUsuario? = null
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(value: String) {
        val error = if (value.isNotBlank()) {
            (EmailValidator.validate(value) as? ValidationResult.Error)?.message
        } else null

        _uiState.update {
            it.copy(
                email = value,
                emailError = error,
                errorMessage = null
            )
        }
    }

    fun onPasswordChanged(value: String) {
        val error = if (value.isBlank()) "Debe ingresar su contraseña" else null
        _uiState.update {
            it.copy(
                password = value,
                passwordError = error,
                errorMessage = null
            )
        }
    }

    fun login() {
        val currentState = _uiState.value

        val emailVal = EmailValidator.validate(currentState.email)
        val emailError = (emailVal as? ValidationResult.Error)?.message

        val passwordError = if (currentState.password.isBlank()) "Debe ingresar su contraseña" else null

        if (emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            authRepository.login(currentState.email, currentState.password)
                .onSuccess { usuario ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loggedInRole = usuario.rol
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Error al iniciar sesión"
                        )
                    }
                }
        }
    }

    fun quickLoginDemo(rol: RolUsuario) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val dummyEmail = when (rol) {
                RolUsuario.DOCENTE -> "carlos.gomez@marcelapaz.cl"
                RolUsuario.ENCARGADO_CRA -> "marcela.valenzuela@marcelapaz.cl"
                RolUsuario.UTP -> "patricia.lagos@marcelapaz.cl"
                RolUsuario.ADMIN_FINANZAS -> "roberto.munoz@marcelapaz.cl"
            }
            val pass = when (rol) {
                RolUsuario.DOCENTE -> "Docente2026!"
                RolUsuario.ENCARGADO_CRA -> "Cra2026!Paz"
                RolUsuario.UTP -> "Utp2026!Paz"
                RolUsuario.ADMIN_FINANZAS -> "Finanzas2026!"
            }

            authRepository.login(dummyEmail, pass)
                .onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false, loggedInRole = user.rol) }
                }
                .onFailure {
                    _uiState.update { state -> state.copy(isLoading = false, loggedInRole = rol) }
                }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
