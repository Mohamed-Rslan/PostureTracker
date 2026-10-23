package com.mobile.posturetracker.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import com.mobile.posturetracker.PostureAnalyzer
import com.mobile.posturetracker.PostureAnalyzer.PostureResult

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Collections

class CameraViewModel(application: Application) : AndroidViewModel(application) {
    var isGoodPosture by mutableStateOf(true)
    var landmarks by mutableStateOf<List<NormalizedLandmark>>(emptyList())
    var postureText by mutableStateOf("Good Posture")
    var postureAlertVisible by mutableStateOf(false)
    var postureIssues by mutableStateOf<List<String>>(emptyList())
    var postureDetails by mutableStateOf<Map<String, Double>>(emptyMap())
    private var poseLandmarker: PoseLandmarker? = null
    private val postureAnalyzer = PostureAnalyzer()
    private var lastPostureStatus = true
    private var badPostureStartTime = 0L
    private val BAD_POSTURE_THRESHOLD_MS = 3000L
    private var frameTimestamp = 0L

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                setupPoseLandmarker()
            } catch (e: IOException) {
                // Handle error
            }
        }
    }

    private fun setupPoseLandmarker() {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("pose_landmarker_heavy.task")
            .setDelegate(Delegate.GPU)
            .build()
        val options = PoseLandmarker.PoseLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setResultListener { result, image -> processResults(result, image) }
            .setErrorListener { }
            .setMinPoseDetectionConfidence(0.5F)
            .setMinTrackingConfidence(0.5F)
            .setMinPosePresenceConfidence(0.5F)
            .build()
        poseLandmarker = PoseLandmarker.createFromOptions(getApplication(), options)
    }

    fun analyzeCameraImage(imageProxy: ImageProxy) {
        try {
            val bitmap = imageProxyToBitmap(imageProxy)
            if (bitmap != null && poseLandmarker != null) {
                val imageRotation = imageProxy.imageInfo.rotationDegrees
                val mpImage = BitmapImageBuilder(bitmap).build()
                frameTimestamp = System.currentTimeMillis()
                poseLandmarker!!.detectAsync(mpImage, frameTimestamp)
            }
        } catch (_: Exception) {
        } finally {
            imageProxy.close()
        }
    }

    private fun processResults(result: PoseLandmarkerResult?, image: MPImage?) {
        if (result == null || image == null) return
        val detectedLandmarks = if (result.landmarks().isEmpty()) emptyList() else ArrayList(result.landmarks()[0])
        landmarks = detectedLandmarks
        if (landmarks.isEmpty()) {
            handlePostureStatus(true)
            postureIssues = listOf("No pose detected")
            postureDetails = emptyMap()
        } else {
            val postureResult: PostureResult = postureAnalyzer.analyzePosture(landmarks)
            handlePostureStatus(postureResult.isGood)
            postureIssues = postureResult.issues
            postureDetails = postureResult.details
        }
    }

    private fun handlePostureStatus(isGood: Boolean) {
        val currentTime = System.currentTimeMillis()
        if (!isGood) {
            if (lastPostureStatus) {
                badPostureStartTime = currentTime
                lastPostureStatus = false
                updatePostureStatus(false)
            } else if (currentTime - badPostureStartTime > BAD_POSTURE_THRESHOLD_MS) {
                postureAlertVisible = true
            }
        } else if (!lastPostureStatus) {
            lastPostureStatus = true
            updatePostureStatus(true)
        }
    }

    private fun updatePostureStatus(isGood: Boolean) {
        isGoodPosture = isGood
        postureText = if (isGood) "Good Posture" else "Fix Your Posture"
        if (isGood) postureAlertVisible = false
    }

    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        // Implement conversion or use your BitmapImageUtils
        return null // Placeholder
    }

    override fun onCleared() {
        super.onCleared()
        poseLandmarker?.close()
    }
}

