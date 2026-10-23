package com.mobile.posturetracker

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mobile.posturetracker.ui.screens.SettingsScreen
import com.mobile.posturetracker.ui.screens.DashboardScreen
import com.mobile.posturetracker.ui.screens.CameraScreen
import com.mobile.posturetracker.ui.screens.ExercisesScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen(navController) }
        composable("camera") { CameraScreen(navController) }
        composable("exercises") { ExercisesScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
    }
}

