package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.prestaaula.di.AppContainer

class AppViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(container.authRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(container.authRepository) as T
            }
            modelClass.isAssignableFrom(SplashViewModel::class.java) -> {
                SplashViewModel(container.authRepository) as T
            }
            modelClass.isAssignableFrom(PerfilViewModel::class.java) -> {
                PerfilViewModel(container.authRepository) as T
            }
            else -> throw IllegalArgumentException("ViewModel no registrado en AppViewModelFactory: ${modelClass.name}")
        }
    }
}
