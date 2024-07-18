package com.yash.servicesinandroid

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "timer_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        val TIME_LEFT_KEY = longPreferencesKey("TIME_LEFT")
    }

    val timeLeftFlow: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[TIME_LEFT_KEY] ?: 1500000L
        }

    suspend fun saveTimeLeft(timeLeft: Long) {
        context.dataStore.edit { preferences ->
            preferences[TIME_LEFT_KEY] = timeLeft
        }
    }
}
