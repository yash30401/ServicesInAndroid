package com.yash.servicesinandroid

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "timer_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        val TIME_LEFT_KEY = longPreferencesKey("TIME_LEFT")
        val EXTRA_TIME_KEY = intPreferencesKey("EXTRA_TIME")
    }

    val timeLeftFlow: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[TIME_LEFT_KEY] ?: 1500000L
        }

    val extraTime: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[EXTRA_TIME_KEY] ?: 0
    }

    suspend fun saveTimeLeft(timeLeft: Long) {
        context.dataStore.edit { preferences ->
            preferences[TIME_LEFT_KEY] = timeLeft
        }
    }

    suspend fun saveExtraTime(extraTime: Int) {
        context.dataStore.edit { preferences ->
            preferences[EXTRA_TIME_KEY] = extraTime
        }
    }
}
