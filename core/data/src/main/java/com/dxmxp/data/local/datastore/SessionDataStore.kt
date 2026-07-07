package com.dxmxp.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

@Singleton
class SessionDataStore @Inject constructor(
    private val context: Context,
    private val cryptoManager: CryptoManager
) {
    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")

    val accessToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[accessTokenKey]?.let { encrypted ->
            try {
                String(cryptoManager.decrypt(android.util.Base64.decode(encrypted, android.util.Base64.DEFAULT)))
            } catch (e: Exception) {
                null
            }
        }
    }

    val refreshToken: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[refreshTokenKey]?.let { encrypted ->
            try {
                String(cryptoManager.decrypt(android.util.Base64.decode(encrypted, android.util.Base64.DEFAULT)))
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        val encryptedAccess = android.util.Base64.encodeToString(
            cryptoManager.encrypt(accessToken.toByteArray()),
            android.util.Base64.DEFAULT
        )
        val encryptedRefresh = android.util.Base64.encodeToString(
            cryptoManager.encrypt(refreshToken.toByteArray()),
            android.util.Base64.DEFAULT
        )
        context.dataStore.edit { preferences ->
            preferences[accessTokenKey] = encryptedAccess
            preferences[refreshTokenKey] = encryptedRefresh
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(accessTokenKey)
            preferences.remove(refreshTokenKey)
        }
    }
}
