package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.prestaaula.data.local.entity.UsuarioEntity
import cl.duoc.prestaaula.domain.model.RolUsuario
import cl.duoc.prestaaula.domain.repository.AuthRepository
import cl.duoc.prestaaula.domain.validation.EmailValidator
import cl.duoc.prestaaula.domain.validation.PasswordValidator
import cl.duoc.prestaaula.domain.validation.RutValidator
import cl.duoc.prestaaula.domain.validation.ValidationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val rut: String = "",
    val rutError: String? = null,
    val nombre: String = "",
    val nombreError: String? = null,
    val apellido: String = "",
    val apellidoError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val rol: RolUsuario = RolUsuario.DOCENTE,
    val password: String = "",
    val passwordError: String? = null,
    val confirmPassword: String = "",
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onRutChanged(value: String) {
        val formatted = if (value.length > 2 && !value.contains("-")) RutValidator.formatRut(value) else value
        val error = if (value.isNotBlank()) {
            (RutValidator.validate(value) as? ValidationResult.Error)?.message
        } else null

        _uiState.update { it.copy(rut = formatted, rutError = error) }
    }

    fun onNombreChanged(value: String) {
        val error = if (value.isBlank()) "El nombre es obligatorio" else null
        _uiState.update { it.copy(nombre = value, nombreError = error) }
    }

    fun onApellidoChanged(value: String) {
        val error = if (value.isBlank()) "El apellido es obligatorio" else null
        _uiState.update { it.copy(apellido = value, apellidoError = error) }
    }

    fun onEmailChanged(value: String) {
        val error = if (value.isNotBlank()) {
            (EmailValidator.validate(value) as? ValidationResult.Error)?.message
        } else null
        _uiState.update { it.copy(email = value, emailError = error) }
    }

    fun onRolChanged(value: RolUsuario) {
        _uiState.update { it.copy(rol = value) }
    }

    fun onPasswordChanged(value: String) {
        val error = if (value.isNotBlank()) {
            (PasswordValidator.validate(value) as? ValidationResult.Error)?.message
        } else null

        val confirmError = if (_uiState.value.confirmPassword.isNotBlank() && _uiState.value.confirmPassword != value) {
            "Las contraseñas no coinciden"
        } else null

        _uiState.update { it.copy(password = value, passwordError = error, confirmPasswordError = confirmError) }
    }

    fun onConfirmPasswordChanged(value: String) {
        val error = if (value != _uiState.value.password) "Las contraseñas no coinciden" else null
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = error) }
    }

    fun register() {
        val state = _uiState.value

        val rutError = (RutValidator.validate(state.rut) as? ValidationResult.Error)?.message
        val nombreError = if (state.nombre.isBlank()) "El nombre es obligatorio" else null
        val apellidoError = if (state.apellido.isBlank()) "El apellido es obligatorio" else null
        val emailError = (EmailValidator.validate(state.email) as? ValidationResult.Error)?.message
        val passwordError = (PasswordValidator.validate(state.password) as? ValidationResult.Error)?.message
        val confirmError = if (state.confirmPassword != state.password) "Las contraseñas no coinciden" else null

        if (rutError != null || nombreError != null || apellidoError != null ||
            emailError != null || passwordError != null || confirmError != null) {
            _uiState.update {
                it.copy(
                    rutError = rutError,
                    nombreError = nombreError,
                    apellidoError = apellidoError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val nuevoUsuario = UsuarioEntity(
                rut = RutValidator.formatRut(state.rut),
                nombre = state.nombre.trim(),
                apellido = state.apellido.trim(),
                email = state.email.trim().lowercase(),
                rol = state.rol,
                passwordHash = ""
            )

            authRepository.register(nuevoUsuario, state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                }
                .onFailure { exc ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = exc.message ?: "Error al registrar usuario") }
                }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
