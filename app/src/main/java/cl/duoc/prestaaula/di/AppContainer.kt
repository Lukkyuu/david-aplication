package cl.duoc.prestaaula.di

import android.content.Context

/**
 * Contenedor de Inyección de Dependencias manual para PrestaAula.
 * Proporciona instancias únicas de repositorios, base de datos y servicios.
 * Se justifica su uso frente a Hilt para facilitar la explicación técnica en la defensa académica.
 */
interface AppContainer {
    // Se completará en Fase 2 con los repositorios locales y DataStore
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    // Implementaciones de repositorios se agregarán en la Fase 2
}
