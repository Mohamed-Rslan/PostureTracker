package com.mobile.posturetracker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mobile.posturetracker.ui.theme.PostureTrackerTheme
import com.mobile.posturetracker.ui.screens.GlassCard

// Placeholder data class
private data class Exercise(val name: String, val description: String)

@Composable
fun ExercisesScreen(navController: NavController) {
    val exercises = remember {
        listOf(
            Exercise("Chin Tucks", "Gently draw your head back while keeping your gaze level. Hold for 5 seconds and release. Repeat 10 times."),
            Exercise("Shoulder Rolls", "Roll your shoulders forward and backward 10 times each."),
            Exercise("Neck Stretches", "Tilt your head to each side and hold for 10 seconds.")
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Exercises",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(exercises) { exercise ->
                GlassCard(modifier = Modifier.clickable { /* TODO: Navigate to detail */ }) {
                    Text(exercise.name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(exercise.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

