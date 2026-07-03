package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "security_prefs")

class SecurityRepository(private val context: Context) {
    
    companion object {
        val PIN_HASH = stringPreferencesKey("pin_hash")
        val FAKE_PIN_HASH = stringPreferencesKey("fake_pin_hash")
        val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val FAILED_ATTEMPTS = intPreferencesKey("failed_attempts")
        val WIPE_THRESHOLD = intPreferencesKey("wipe_threshold")
    }

    val pinHashFlow: Flow<String?> = context.dataStore.data.map { prefs -> prefs[PIN_HASH] }
    val fakePinHashFlow: Flow<String?> = context.dataStore.data.map { prefs -> prefs[FAKE_PIN_HASH] }
    val biometricEnabledFlow: Flow<Boolean> = context.dataStore.data.map { prefs -> prefs[BIOMETRIC_ENABLED] ?: false }
    val failedAttemptsFlow: Flow<Int> = context.dataStore.data.map { prefs -> prefs[FAILED_ATTEMPTS] ?: 0 }
    val wipeThresholdFlow: Flow<Int> = context.dataStore.data.map { prefs -> prefs[WIPE_THRESHOLD] ?: 5 }

    suspend fun setPin(pinHash: String) {
        context.dataStore.edit { prefs -> prefs[PIN_HASH] = pinHash }
    }

    suspend fun setFakePin(pinHash: String) {
        context.dataStore.edit { prefs -> prefs[FAKE_PIN_HASH] = pinHash }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[BIOMETRIC_ENABLED] = enabled }
    }

    suspend fun incrementFailedAttempts(): Int {
        var currentAttempts = 0
        context.dataStore.edit { prefs ->
            currentAttempts = (prefs[FAILED_ATTEMPTS] ?: 0) + 1
            prefs[FAILED_ATTEMPTS] = currentAttempts
        }
        return currentAttempts
    }

    suspend fun resetFailedAttempts() {
        context.dataStore.edit { prefs -> prefs[FAILED_ATTEMPTS] = 0 }
    }

    suspend fun setWipeThreshold(threshold: Int) {
        context.dataStore.edit { prefs -> prefs[WIPE_THRESHOLD] = threshold }
    }
    
    suspend fun selfDestruct() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
