package com.mobile.posturetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mobile.posturetracker.ui.theme.PostureTrackerTheme
import com.mobile.posturetracker.viewmodel.PostureViewModel
import com.mobile.posturetracker.data.PostureSession

@Composable
fun DashboardScreen(navController: NavController, viewModel: PostureViewModel = viewModel()) {
    val sessions = viewModel.sessions.collectAsState().value
    val today = sessions.firstOrNull { isToday(it.startTime) }
    val goodPosture = today?.goodPostureDuration ?: 0L
    val badPosture = today?.badPostureDuration ?: 0L
    val total = goodPosture + badPosture
    val score = if (total > 0) (goodPosture * 100 / total).toInt() else 100

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        GlassCard {
            Text(
                text = "Today's Posture Score",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("$score/100", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Good: ${goodPosture/60}s, Bad: ${badPosture/60}s", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))
        GlassCard {
            Text("Recent Activity", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(8.dp))
            sessions.take(3).forEach {
                Text("Session: Good ${it.goodPostureDuration/60}s, Bad ${it.badPostureDuration/60}s", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

fun isToday(timestamp: Long): Boolean {
    val now = java.util.Calendar.getInstance()
    val cal = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
    return now.get(java.util.Calendar.YEAR) == cal.get(java.util.Calendar.YEAR) &&
           now.get(java.util.Calendar.DAY_OF_YEAR) == cal.get(java.util.Calendar.DAY_OF_YEAR)
}
