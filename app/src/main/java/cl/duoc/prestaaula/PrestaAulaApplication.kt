package cl.duoc.prestaaula

import android.app.Application
import cl.duoc.prestaaula.di.AppContainer
import cl.duoc.prestaaula.di.DefaultAppContainer

class PrestaAulaApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
