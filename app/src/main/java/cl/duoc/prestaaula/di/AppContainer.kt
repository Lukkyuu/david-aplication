package cl.duoc.prestaaula.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.duoc.prestaaula.data.datastore.UserSessionDataStore
import cl.duoc.prestaaula.data.local.PrestaAulaDatabase
import cl.duoc.prestaaula.data.local.transaction.RoomTransactionRunner
import cl.duoc.prestaaula.data.local.transaction.TransactionRunner
import cl.duoc.prestaaula.data.repository.AuthRepositoryImpl
import cl.duoc.prestaaula.data.repository.BloqueHorarioRepositoryImpl
import cl.duoc.prestaaula.data.repository.EquipoRepositoryImpl
import cl.duoc.prestaaula.data.repository.PrestamoRepositoryImpl
import cl.duoc.prestaaula.data.repository.ReservaRepositoryImpl
import cl.duoc.prestaaula.data.seed.DatabaseSeeder
import cl.duoc.prestaaula.domain.repository.AuthRepository
import cl.duoc.prestaaula.domain.repository.BloqueHorarioRepository
import cl.duoc.prestaaula.domain.repository.EquipoRepository
import cl.duoc.prestaaula.domain.repository.PrestamoRepository
import cl.duoc.prestaaula.domain.repository.ReservaRepository
import cl.duoc.prestaaula.domain.usecase.RegistrarDevolucionUseCase
import cl.duoc.prestaaula.domain.usecase.RegistrarPrestamoUseCase
import cl.duoc.prestaaula.domain.usecase.ReservarEquipoUseCase
import cl.duoc.prestaaula.domain.usecase.VencerReservasUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Contenedor de Inyección de Dependencias manual para PrestaAula.
 * Proporciona instancias únicas de repositorios, base de datos y servicios.
 * Se justifica su uso frente a Hilt para facilitar la explicación técnica en la defensa académica.
 */
interface AppContainer {
    val database: PrestaAulaDatabase
    val transactionRunner: TransactionRunner
    val sessionDataStore: UserSessionDataStore
    val authRepository: AuthRepository
    val equipoRepository: EquipoRepository
    val reservaRepository: ReservaRepository
    val prestamoRepository: PrestamoRepository
    val bloqueHorarioRepository: BloqueHorarioRepository

    val reservarEquipoUseCase: ReservarEquipoUseCase
    val registrarPrestamoUseCase: RegistrarPrestamoUseCase
    val registrarDevolucionUseCase: RegistrarDevolucionUseCase
    val vencerReservasUseCase: VencerReservasUseCase
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val database: PrestaAulaDatabase by lazy {
        Room.databaseBuilder(
            context,
            PrestaAulaDatabase::class.java,
            "prestaaula.db"
        )
        .fallbackToDestructiveMigration()
        .build().also { dbInstance ->
            CoroutineScope(Dispatchers.IO).launch {
                DatabaseSeeder.seedDatabaseIfEmpty(dbInstance)
            }
        }
    }

    override val transactionRunner: TransactionRunner by lazy {
        RoomTransactionRunner(database)
    }

    override val sessionDataStore: UserSessionDataStore by lazy {
        UserSessionDataStore(context)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            usuarioDao = database.usuarioDao(),
            sessionDataStore = sessionDataStore
        )
    }

    override val equipoRepository: EquipoRepository by lazy {
        EquipoRepositoryImpl(
            equipoDao = database.equipoDao()
        )
    }

    override val reservaRepository: ReservaRepository by lazy {
        ReservaRepositoryImpl(
            reservaDao = database.reservaDao(),
            equipoDao = database.equipoDao(),
            transactionRunner = transactionRunner
        )
    }

    override val prestamoRepository: PrestamoRepository by lazy {
        PrestamoRepositoryImpl(
            prestamoDao = database.prestamoDao(),
            equipoDao = database.equipoDao(),
            reservaDao = database.reservaDao(),
            transactionRunner = transactionRunner
        )
    }

    override val bloqueHorarioRepository: BloqueHorarioRepository by lazy {
        BloqueHorarioRepositoryImpl(
            bloqueHorarioDao = database.bloqueHorarioDao(),
            disponibilidadDocenteDao = database.disponibilidadDocenteDao()
        )
    }

    override val reservarEquipoUseCase: ReservarEquipoUseCase by lazy {
        ReservarEquipoUseCase(reservaRepository, equipoRepository)
    }

    override val registrarPrestamoUseCase: RegistrarPrestamoUseCase by lazy {
        RegistrarPrestamoUseCase(prestamoRepository)
    }

    override val registrarDevolucionUseCase: RegistrarDevolucionUseCase by lazy {
        RegistrarDevolucionUseCase(prestamoRepository)
    }

    override val vencerReservasUseCase: VencerReservasUseCase by lazy {
        VencerReservasUseCase(reservaRepository)
    }
}
