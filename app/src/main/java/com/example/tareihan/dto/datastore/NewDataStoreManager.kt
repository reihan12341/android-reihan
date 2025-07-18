package com.example.tareihan.dto.datastore;
import android.content.Context;
import androidx.datastore.preferences.core.*;
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
public class NewDataStoreManager (private val context: Context) {

    // Buat DataStore instance
    private val Context.dataStore by preferencesDataStore(name = "session_prefs")

    companion object {
        val TOKEN_KEY = stringPreferencesKey("token")
        val ID_KEY = stringPreferencesKey("id")
        val EMAIL_KEY = stringPreferencesKey("email")
        val NAME_KEY = stringPreferencesKey("name")
        val JABATAN_KEY = stringPreferencesKey("jabatan")
        val JENIS_KELAMIN_KEY = stringPreferencesKey("jenis_kelamin")

        @Volatile
        private var INSTANCE: NewDataStoreManager? = null

        fun getInstance(context: Context): NewDataStoreManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: NewDataStoreManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    // Save token
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
        }
    }

    // Save user data
    suspend fun saveUserData(
        id: String,
        email: String,
        name: String,
        jabatan: String,
        jenisKelamin: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[ID_KEY] = id
            prefs[EMAIL_KEY] = email
            prefs[NAME_KEY] = name
            prefs[JABATAN_KEY] = jabatan
            prefs[JENIS_KELAMIN_KEY] = jenisKelamin
        }
    }

    // Clear everything
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }

    // Flows to observe
    val authTokenFlow: Flow<String?> = context.dataStore.data.map { it[TOKEN_KEY] }
    val idFlow: Flow<String?> = context.dataStore.data.map { it[ID_KEY] }
    val emailFlow: Flow<String?> = context.dataStore.data.map { it[EMAIL_KEY] }
    val nameFlow: Flow<String?> = context.dataStore.data.map { it[NAME_KEY] }
    val jabatanFlow: Flow<String?> = context.dataStore.data.map { it[JABATAN_KEY] }
    val jenisKelaminFlow: Flow<String?> = context.dataStore.data.map { it[JENIS_KELAMIN_KEY] }

}
