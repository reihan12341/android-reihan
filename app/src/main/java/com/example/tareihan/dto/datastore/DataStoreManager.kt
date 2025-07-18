package com.example.tareihan.dto.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class DataStoreManager(private val context: Context) {



    private val dataStore: DataStore<Preferences> by lazy {
        PreferenceDataStoreFactory.create(
            produceFile = { context.preferencesDataStoreFile("auth_prefs") }
        )
    }

    companion object {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_JABATAN = stringPreferencesKey("user_jabatan")
        val USER_JENIS_KELAMIN = stringPreferencesKey("user_jenis_kelamin")
    }

    val userNameFlow: Flow<String?> = dataStore.data.map { prefs -> prefs[USER_NAME] }
    val userJabatanFlow: Flow<String?> = dataStore.data.map { prefs -> prefs[USER_JABATAN] }
// dan seterusnya


    val authTokenFlow: Flow<String?> = dataStore.data
        .map { prefs -> prefs[AUTH_TOKEN] }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { prefs ->
            prefs[AUTH_TOKEN] = token
        }
    }

    suspend fun clearAuthToken() {
        dataStore.edit { prefs ->
            prefs.remove(AUTH_TOKEN)
        }
    }

    suspend fun saveUserData(
        id: String,
        name: String,
        email: String,
        jabatan: String,
        jenisKelamin: String
    ) {
        dataStore.edit { prefs ->
            prefs[USER_ID] = id
            prefs[USER_NAME] = name
            prefs[USER_EMAIL] = email
            prefs[USER_JABATAN] = jabatan
            prefs[USER_JENIS_KELAMIN] = jenisKelamin
        }
    }

    suspend fun clearUserData() {
        dataStore.edit { prefs ->
            prefs.remove(USER_ID)
            prefs.remove(USER_NAME)
            prefs.remove(USER_EMAIL)
            prefs.remove(USER_JABATAN)
            prefs.remove(USER_JENIS_KELAMIN)
        }
    }


}

