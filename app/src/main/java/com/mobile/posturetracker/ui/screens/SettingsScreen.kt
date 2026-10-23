package com.mobile.posturetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mobile.posturetracker.ui.theme.PostureTrackerTheme
import com.mobile.posturetracker.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(navController: NavController, viewModel: SettingsViewModel = viewModel()) {
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()
    val reminderInterval by viewModel.reminderInterval.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()
    val showSkeleton by viewModel.showSkeleton.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        GlassCard {
            NotificationSettingsSection(
                notificationsEnabled = notificationsEnabled,
                vibrationEnabled = vibrationEnabled,
                reminderInterval = reminderInterval,
                onNotificationsChanged = viewModel::setNotificationsEnabled,
                onVibrationChanged = viewModel::setVibrationEnabled,
                onReminderIntervalChanged = viewModel::setReminderInterval
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        GlassCard {
            DetectionSettingsSection()
        }
        Spacer(modifier = Modifier.height(16.dp))
        GlassCard {
            AppearanceSettingsSection(
                darkMode = darkMode,
                showSkeleton = showSkeleton,
                onDarkModeChanged = viewModel::setDarkMode,
                onShowSkeletonChanged = viewModel::setShowSkeleton
            )
        }
    }
}

@Composable
fun NotificationSettingsSection(
    notificationsEnabled: Boolean,
    vibrationEnabled: Boolean,
    reminderInterval: Int,
    onNotificationsChanged: (Boolean) -> Unit,
    onVibrationChanged: (Boolean) -> Unit,
    onReminderIntervalChanged: (Int) -> Unit
) {
    Column {
        Text("Notifications", style = MaterialTheme.typography.titleLarge)
        SwitchRow(
            label = "Enable Notifications",
            checked = notificationsEnabled,
            onCheckedChange = onNotificationsChanged
        )
        SwitchRow(
            label = "Enable Vibration",
            checked = vibrationEnabled,
            onCheckedChange = onVibrationChanged
        )
        Text("Reminder Interval", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
        Slider(
            value = reminderInterval.toFloat(),
            onValueChange = { onReminderIntervalChanged(it.toInt()) },
            valueRange = 0f..60f,
            steps = 11,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Text("${reminderInterval} minutes", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.End))
    }
}

@Composable
fun DetectionSettingsSection() {
    var sensitivity by remember { mutableStateOf(5f) }
    var badPostureDuration by remember { mutableStateOf(3f) }
    Column {
        Text("Posture Detection", style = MaterialTheme.typography.titleLarge)
        Text("Detection Sensitivity", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
        Slider(
            value = sensitivity,
            onValueChange = { sensitivity = it },
            valueRange = 1f..10f,
            steps = 8,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Text("Medium (${sensitivity.toInt()})", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.End))
        Text("Alert After Bad Posture Duration", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 16.dp))
        Slider(
            value = badPostureDuration,
            onValueChange = { badPostureDuration = it },
            valueRange = 1f..10f,
            steps = 8,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Text("${badPostureDuration.toInt()} seconds", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.End))
    }
}

@Composable
fun AppearanceSettingsSection(
    darkMode: Boolean,
    showSkeleton: Boolean,
    onDarkModeChanged: (Boolean) -> Unit,
    onShowSkeletonChanged: (Boolean) -> Unit
) {
    Column {
        Text("Appearance", style = MaterialTheme.typography.titleLarge)
        SwitchRow(
            label = "Dark Mode",
            checked = darkMode,
            onCheckedChange = onDarkModeChanged
        )
        SwitchRow(
            label = "Show Skeleton Overlay",
            checked = showSkeleton,
            onCheckedChange = onShowSkeletonChanged
        )
    }
}

@Composable
fun SwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Text(label, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
