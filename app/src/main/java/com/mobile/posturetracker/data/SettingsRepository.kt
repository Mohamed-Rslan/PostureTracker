package com.mobile.posturetracker.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore by preferencesDataStore(name = "settings")

object SettingsKeys {
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
    val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
    val REMINDER_INTERVAL = intPreferencesKey("reminder_interval")
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    val SHOW_SKELETON = booleanPreferencesKey("show_skeleton")
}

class SettingsRepository(private val context: Context) {
    val notificationsEnabled: Flow<Boolean> = context.settingsDataStore.data.map { it[SettingsKeys.NOTIFICATIONS_ENABLED] ?: true }
    val vibrationEnabled: Flow<Boolean> = context.settingsDataStore.data.map { it[SettingsKeys.VIBRATION_ENABLED] ?: true }
    val reminderInterval: Flow<Int> = context.settingsDataStore.data.map { it[SettingsKeys.REMINDER_INTERVAL] ?: 30 }
    val darkMode: Flow<Boolean> = context.settingsDataStore.data.map { it[SettingsKeys.DARK_MODE] ?: false }
    val showSkeleton: Flow<Boolean> = context.settingsDataStore.data.map { it[SettingsKeys.SHOW_SKELETON] ?: true }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.NOTIFICATIONS_ENABLED] = enabled }
    }
    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.VIBRATION_ENABLED] = enabled }
    }
    suspend fun setReminderInterval(interval: Int) {
        context.settingsDataStore.edit { it[SettingsKeys.REMINDER_INTERVAL] = interval }
    }
    suspend fun setDarkMode(enabled: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.DARK_MODE] = enabled }
    }
    suspend fun setShowSkeleton(enabled: Boolean) {
        context.settingsDataStore.edit { it[SettingsKeys.SHOW_SKELETON] = enabled }
    }
}
