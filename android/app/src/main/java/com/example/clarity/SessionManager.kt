package com.example.clarity

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

class SessionManager(private val context: Context) {

    private object Keys {
        val USER_NAME_KEY = stringPreferencesKey("user_name_key")
        val USER_ID_KEY = intPreferencesKey("user_id_key")
        // Add more keys as needed
    }

    private val userName = stringPreferencesKey("user_name_key")

    suspend fun setUserName(value: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.USER_NAME_KEY] = value
        }
    }

    suspend fun setUserId(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[Keys.USER_ID_KEY] = value
        }
    }

    // Read a value from the Preferences DataStore
    suspend fun getUserName(): String {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[Keys.USER_NAME_KEY] ?: ""
            }
            .first() // This suspending function retrieves the first emitted value
    }

    suspend fun getUserId(): Int {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[Keys.USER_ID_KEY] ?: 0
            }
            .first() // This suspending function retrieves the first emitted value
    }
}