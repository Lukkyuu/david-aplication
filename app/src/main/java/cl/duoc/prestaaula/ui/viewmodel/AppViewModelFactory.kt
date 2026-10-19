package cl.duoc.prestaaula.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.prestaaula.di.AppContainer

class AppViewModelFactory(
    private val container: AppContainer,
    private val extraArgLong: Long = 0L,
    private val extraArgString: String? = null
) : ViewModelProvider.Factory {
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
            modelClass.isAssignableFrom(DocenteHomeViewModel::class.java) -> {
                DocenteHomeViewModel(container.authRepository, container.reservaRepository, container.prestamoRepository) as T
            }
            modelClass.isAssignableFrom(DisponibilidadViewModel::class.java) -> {
                DisponibilidadViewModel(container.equipoRepository, container.bloqueHorarioRepository) as T
            }
            modelClass.isAssignableFrom(ReservaFormViewModel::class.java) -> {
                ReservaFormViewModel(extraArgLong, container.authRepository, container.equipoRepository, container.bloqueHorarioRepository, container.reservarEquipoUseCase) as T
            }
            modelClass.isAssignableFrom(MisReservasViewModel::class.java) -> {
                MisReservasViewModel(container.authRepository, container.reservaRepository) as T
            }
            modelClass.isAssignableFrom(InventarioViewModel::class.java) -> {
                InventarioViewModel(container.equipoRepository) as T
            }
            modelClass.isAssignableFrom(EquipoDetalleViewModel::class.java) -> {
                EquipoDetalleViewModel(extraArgLong, container.equipoRepository, container.prestamoRepository) as T
            }
            modelClass.isAssignableFrom(EquipoFormViewModel::class.java) -> {
                val equipoIdLong = extraArgString?.toLongOrNull() ?: extraArgLong.takeIf { it > 0L }
                EquipoFormViewModel(equipoIdLong, container.equipoRepository) as T
            }
            else -> throw IllegalArgumentException("ViewModel no registrado en AppViewModelFactory: ${modelClass.name}")
        }
    }
}
