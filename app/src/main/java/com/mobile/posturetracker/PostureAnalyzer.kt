package com.mobile.posturetracker

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt
import java.lang.Math

class PostureAnalyzer {
    data class PostureResult(
        val isGood: Boolean,
        val issues: List<String>,
        val details: Map<String, Double>
    )

    companion object {
        // MediaPipe landmark indices
        private const val NOSE = 0
        private const val LEFT_EAR = 7
        private const val RIGHT_EAR = 8
        private const val LEFT_SHOULDER = 11
        private const val RIGHT_SHOULDER = 12
        private const val LEFT_HIP = 23
        private const val RIGHT_HIP = 24
        private const val LEFT_EYE = 1
        private const val RIGHT_EYE = 4
    }

    // Default thresholds (tunable)
    private var neckTiltThreshold = 15.0 // degrees
    private var headForwardThreshold = 0.12 // normalized distance
    private var shoulderImbalanceThreshold = 0.05 // normalized vertical difference
    private var spineCurveThreshold = 0.10 // normalized horizontal deviation

    fun updateSensitivity(sensitivity: Int) {
        // Lower thresholds for higher sensitivity
        val factor = 1.5 - (sensitivity / 100.0)
        neckTiltThreshold = 15.0 * factor
        headForwardThreshold = 0.12 * factor
        shoulderImbalanceThreshold = 0.05 * factor
        spineCurveThreshold = 0.10 * factor
    }

    fun analyzePosture(landmarks: List<NormalizedLandmark>?): PostureResult {
        if (landmarks == null || landmarks.size < 33) {
            return PostureResult(true, listOf("No pose detected"), emptyMap())
        }
        val issues = mutableListOf<String>()
        val details = mutableMapOf<String, Double>()

        // Neck tilt (angle between shoulders and ears)
        val leftShoulder = landmarks[LEFT_SHOULDER]
        val rightShoulder = landmarks[RIGHT_SHOULDER]
        val leftEar = landmarks[LEFT_EAR]
        val rightEar = landmarks[RIGHT_EAR]
        val shoulderMidX = (leftShoulder.x() + rightShoulder.x()) / 2
        val shoulderMidY = (leftShoulder.y() + rightShoulder.y()) / 2
        val earMidX = (leftEar.x() + rightEar.x()) / 2
        val earMidY = (leftEar.y() + rightEar.y()) / 2
        val neckAngle = calculateAngle(shoulderMidX, shoulderMidY, earMidX, earMidY)
        details["Neck Tilt Angle"] = neckAngle
        if (neckAngle > neckTiltThreshold) issues.add("Neck tilt/slouching detected")

        // Head forward (horizontal distance between ear midpoint and shoulder midpoint)
        val headForwardDist = abs(earMidX - shoulderMidX)
        details["Head Forward Distance"] = headForwardDist.toDouble()
        if (headForwardDist > headForwardThreshold) issues.add("Forward head posture detected")

        // Shoulder imbalance (vertical difference between shoulders)
        val shoulderImbalance = abs(leftShoulder.y() - rightShoulder.y())
        details["Shoulder Imbalance"] = shoulderImbalance.toDouble()
        if (shoulderImbalance > shoulderImbalanceThreshold) issues.add("Shoulder imbalance detected")

        // Spine curve (horizontal deviation between mid-shoulder and mid-hip)
        val leftHip = landmarks[LEFT_HIP]
        val rightHip = landmarks[RIGHT_HIP]
        val hipMidX = (leftHip.x() + rightHip.x()) / 2
        val hipMidY = (leftHip.y() + rightHip.y()) / 2
        val spineCurve = abs(shoulderMidX - hipMidX)
        details["Spine Curve"] = spineCurve.toDouble()
        if (spineCurve > spineCurveThreshold) issues.add("Spine misalignment detected")

        // Head tilt (angle between eyes)
        val leftEye = landmarks[LEFT_EYE]
        val rightEye = landmarks[RIGHT_EYE]
        val headTiltAngle = calculateAngle(leftEye.x(), leftEye.y(), rightEye.x(), rightEye.y())
        details["Head Tilt Angle"] = headTiltAngle
        if (headTiltAngle > 10.0) issues.add("Head tilt detected")

        val isGood = issues.isEmpty()
        return PostureResult(isGood, issues, details)
    }

    private fun calculateAngle(x1: Float, y1: Float, x2: Float, y2: Float): Double {
        val dx = x2 - x1
        val dy = y2 - y1
        val angleRadians = atan2(dx, dy)
        return abs(Math.toDegrees(angleRadians.toDouble()))
    }
}
