package cl.duoc.prestaaula.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cl.duoc.prestaaula.domain.model.RolUsuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

data class UserSession(
    val userId: Long = 0L,
    val rut: String = "",
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val rol: RolUsuario? = null,
    val isLoggedIn: Boolean = false
) {
    val nombreCompleto: String get() = "$nombre $apellido".trim()
}

class UserSessionDataStore(private val context: Context) {

    companion object {
        val KEY_USER_ID = longPreferencesKey("user_id")
        val KEY_RUT = stringPreferencesKey("user_rut")
        val KEY_NOMBRE = stringPreferencesKey("user_nombre")
        val KEY_APELLIDO = stringPreferencesKey("user_apellido")
        val KEY_EMAIL = stringPreferencesKey("user_email")
        val KEY_ROL = stringPreferencesKey("user_rol")
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    }

    val userSession: Flow<UserSession> = context.dataStore.data.map { prefs ->
        val isLoggedIn = prefs[KEY_IS_LOGGED_IN] ?: false
        val rolString = prefs[KEY_ROL]
        val rol = rolString?.let { runCatching { enumValueOf<RolUsuario>(it) }.getOrNull() }

        UserSession(
            userId = prefs[KEY_USER_ID] ?: 0L,
            rut = prefs[KEY_RUT] ?: "",
            nombre = prefs[KEY_NOMBRE] ?: "",
            apellido = prefs[KEY_APELLIDO] ?: "",
            email = prefs[KEY_EMAIL] ?: "",
            rol = rol,
            isLoggedIn = isLoggedIn
        )
    }

    suspend fun saveSession(
        userId: Long,
        rut: String,
        nombre: String,
        apellido: String,
        email: String,
        rol: RolUsuario
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_USER_ID] = userId
            prefs[KEY_RUT] = rut
            prefs[KEY_NOMBRE] = nombre
            prefs[KEY_APELLIDO] = apellido
            prefs[KEY_EMAIL] = email
            prefs[KEY_ROL] = rol.name
            prefs[KEY_IS_LOGGED_IN] = true
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
