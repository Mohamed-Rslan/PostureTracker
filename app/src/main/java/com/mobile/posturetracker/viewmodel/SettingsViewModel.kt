package com.mobile.posturetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.posturetracker.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(application)

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    private val _vibrationEnabled = MutableStateFlow(true)
    val vibrationEnabled: StateFlow<Boolean> = _vibrationEnabled.asStateFlow()

    private val _reminderInterval = MutableStateFlow(30)
    val reminderInterval: StateFlow<Int> = _reminderInterval.asStateFlow()

    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()

    private val _showSkeleton = MutableStateFlow(true)
    val showSkeleton: StateFlow<Boolean> = _showSkeleton.asStateFlow()

    init {
        viewModelScope.launch {
            repository.notificationsEnabled.collectLatest { _notificationsEnabled.value = it }
        }
        viewModelScope.launch {
            repository.vibrationEnabled.collectLatest { _vibrationEnabled.value = it }
        }
        viewModelScope.launch {
            repository.reminderInterval.collectLatest { _reminderInterval.value = it }
        }
        viewModelScope.launch {
            repository.darkMode.collectLatest { _darkMode.value = it }
        }
        viewModelScope.launch {
            repository.showSkeleton.collectLatest { _showSkeleton.value = it }
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setNotificationsEnabled(enabled) }
    }
    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch { repository.setVibrationEnabled(enabled) }
    }
    fun setReminderInterval(interval: Int) {
        viewModelScope.launch { repository.setReminderInterval(interval) }
    }
    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { repository.setDarkMode(enabled) }
    }
    fun setShowSkeleton(enabled: Boolean) {
        viewModelScope.launch { repository.setShowSkeleton(enabled) }
    }
}

