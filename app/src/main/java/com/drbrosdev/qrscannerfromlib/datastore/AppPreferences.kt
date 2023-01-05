package com.drbrosdev.qrscannerfromlib.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/*
The object is scoped to the context, meaning it can be accessed by the context anywhere
the context can exist. The property delegate makes sure this is a singleton.
 */
val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "prefs")

/*
Class to encapsulate datastore functionality.
 */
class AppPreferences(private val dataStore: DataStore<Preferences>) {
    val isFirstLaunch: Flow<Boolean>
        get() = dataStore.data.map { preferences ->
            preferences[FIRST_LAUNCH] ?: false
        }

    val showSupport: Flow<Int>
        get() = dataStore.data.map {
            it[SHOW_SUPPORT] ?: 7
        }

    val showRateDialog: Flow<Int>
        get() = dataStore.data.map {
            it[SHOW_REVIEW_DIALOG] ?: 0
        }

    suspend fun firstLaunchComplete() {
        dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH] = true
        }
    }

    suspend fun incrementReviewDialog() {
        dataStore.edit { preferences ->
            var current = preferences[SHOW_REVIEW_DIALOG] ?: 0
            if (current == 10) return@edit
            current += 1
            preferences[SHOW_REVIEW_DIALOG] = current
        }
    }

    suspend fun incrementSupportKey() {
        dataStore.edit {
            var current = it[SHOW_SUPPORT] ?: 7
            current += 1
            it[SHOW_SUPPORT] = current
        }
    }

    private companion object {
        /*
        Keys for all prefs defined here. Keys are typed so for int prefs use int key,
        for string prefs use string key etc.
         */
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        val SHOW_SUPPORT = intPreferencesKey("show_support")
        val SHOW_REVIEW_DIALOG = intPreferencesKey("show_review_dialog")
    }
}