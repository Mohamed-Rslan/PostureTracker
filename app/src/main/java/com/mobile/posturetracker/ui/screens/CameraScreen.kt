package com.mobile.posturetracker.ui.screens

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.camera.core.Preview
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.mobile.posturetracker.viewmodel.CameraViewModel
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import java.util.concurrent.Executors

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CameraScreen(navController: NavController, viewModel: CameraViewModel = viewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isGoodPosture = viewModel.isGoodPosture
    val postureText = viewModel.postureText
    val postureAlertVisible = viewModel.postureAlertVisible
    val landmarks = viewModel.landmarks
    val postureIssues = viewModel.postureIssues
    val postureDetails = viewModel.postureDetails
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Camera",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        GlassCard {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                            // ImageAnalysis use case for real-time frame analysis
                            val imageAnalysis = androidx.camera.core.ImageAnalysis.Builder()
                                .setBackpressureStrategy(androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build().apply {
                                    setAnalyzer(cameraExecutor, viewModel::analyzeCameraImage)
                                }
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner, cameraSelector, preview, imageAnalysis
                            )
                        }, ctx.mainExecutor)
                        previewView
                    },
                    modifier = Modifier.fillMaxSize()
                )
                // Draw posture overlay using landmarks
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawLandmarksOverlay(landmarks)
                }
                if (postureAlertVisible) {
                    Text(
                        text = "Fix Your Posture!",
                        color = Color.Red,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(postureText, style = MaterialTheme.typography.bodyLarge)
            if (postureIssues.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    postureIssues.forEach { issue ->
                        Text("• $issue", color = Color.Red, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            if (postureDetails.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    postureDetails.forEach { (key, value) ->
                        Text("$key: ${"%.2f".format(value)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
        }
    }
}

fun androidx.compose.ui.graphics.drawscope.DrawScope.drawLandmarksOverlay(landmarks: List<NormalizedLandmark>) {
    if (landmarks.isEmpty()) return
    // Skeleton connections (upper body focus)
    val poseConnections = listOf(
        // Face
        0 to 1, 1 to 2, 2 to 3, 3 to 7, 0 to 4, 4 to 5, 5 to 6, 6 to 8,
        // Torso
        11 to 12, 11 to 23, 12 to 24, 23 to 24,
        // Left arm
        11 to 13, 13 to 15, 15 to 17, 15 to 19, 15 to 21,
        // Right arm
        12 to 14, 14 to 16, 16 to 18, 16 to 20, 16 to 22
    )
    val headLandmarks = setOf(0, 7, 8, 11, 12)
    // Draw connections
    poseConnections.forEach { (start, end) ->
        if (start < landmarks.size && end < landmarks.size) {
            val s = landmarks[start]
            val e = landmarks[end]
            drawLine(
                color = Color.Cyan.copy(alpha = 0.5f),
                start = androidx.compose.ui.geometry.Offset(s.x() * size.width, s.y() * size.height),
                end = androidx.compose.ui.geometry.Offset(e.x() * size.width, e.y() * size.height),
                strokeWidth = 6f,
                alpha = 0.7f
            )
        }
    }
    // Draw landmarks
    for (i in landmarks.indices) {
        val l = landmarks[i]
        val x = l.x() * size.width
        val y = l.y() * size.height
        if (i in headLandmarks) {
            drawCircle(
                color = Color(0xFF80D8FF).copy(alpha = 0.85f),
                radius = 14f,
                center = androidx.compose.ui.geometry.Offset(x, y),
                style = Stroke(width = 6f)
            )
            drawCircle(
                color = Color(0xFF00B8D4).copy(alpha = 0.7f),
                radius = 8f,
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
        } else {
            drawCircle(
                color = Color.White.copy(alpha = 0.7f),
                radius = 7f,
                center = androidx.compose.ui.geometry.Offset(x, y)
            )
        }
    }
    // Draw neck line (ear midpoint to shoulder midpoint) with highlight
    if (landmarks.size > 12) {
        val leftEar = landmarks[7]
        val rightEar = landmarks[8]
        val leftShoulder = landmarks[11]
        val rightShoulder = landmarks[12]
        val earMidX = (leftEar.x() + rightEar.x()) / 2 * size.width
        val earMidY = (leftEar.y() + rightEar.y()) / 2 * size.height
        val shoulderMidX = (leftShoulder.x() + rightShoulder.x()) / 2 * size.width
        val shoulderMidY = (leftShoulder.y() + rightShoulder.y()) / 2 * size.height
        drawLine(
            color = Color(0xFF00E676).copy(alpha = 0.8f),
            start = androidx.compose.ui.geometry.Offset(earMidX, earMidY),
            end = androidx.compose.ui.geometry.Offset(shoulderMidX, shoulderMidY),
            strokeWidth = 10f,
            alpha = 0.9f
        )
    }
}
