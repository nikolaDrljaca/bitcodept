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
    //all stored values are retrieved as flows to be collected
    //and this is how a value is retrieved by its key.
    val showInAppReviewRequest: Flow<Int?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_REVIEW]
        }

    val isFirstLaunch: Flow<Boolean>
        get() = dataStore.data.map { preferences ->
            preferences[FIRST_LAUNCH] ?: false
        }

    val hasSeenFirstUpdate: Flow<Boolean>
        get() = dataStore.data.map { preferences ->
            preferences[FIRST_UPDATE] ?: false
        }

    suspend fun incrementReviewKey() {
        //this is how values are stored/updated
        dataStore.edit { preferences ->
            var current = preferences[KEY_REVIEW] ?: 0
            current += 1
            preferences[KEY_REVIEW] = current
        }
    }

    suspend fun firstLaunchComplete() {
        dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH] = true
        }
    }

    suspend fun seenFirstUpdateComplete() {
        dataStore.edit { preferences ->
            preferences[FIRST_UPDATE] = true
        }
    }

    private companion object {
        /*
        Keys for all prefs defined here. Keys are typed so for int prefs use int key,
        for string prefs use string key etc.
         */
        val KEY_REVIEW = intPreferencesKey("key_review")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        val FIRST_UPDATE = booleanPreferencesKey("update_one")
    }
}