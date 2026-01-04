package com.clarice.burrow.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// DataStore extension
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "burrow_prefs")

// TokenManager - Handles secure token storage using DataStore
class TokenManager(private val context: Context) {

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val USERNAME_KEY = stringPreferencesKey("username")
    }

    // Save authentication token
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    // Get authentication token as Flow
    fun getToken(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }
    }

    // Get authentication token (suspend function)
    suspend fun getTokenDirect(): String? {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }.first()
    }

    // Save user info
    suspend fun saveUserInfo(userId: Int, username: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId.toString()
            preferences[USERNAME_KEY] = username
        }
    }

    // Get user ID
    fun getUserId(): Flow<Int?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_ID_KEY]?.toIntOrNull()
        }
    }

    // Get username
    fun getUsername(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USERNAME_KEY]
        }
    }

    // Clear all stored data (logout)
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    // Check if user is logged in
    fun isLoggedIn(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY] != null
        }
    }
}