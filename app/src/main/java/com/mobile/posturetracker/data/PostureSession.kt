package com.mobile.posturetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "posture_sessions")
data class PostureSession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: Long,
    val endTime: Long,
    val goodPostureDuration: Long,
    val badPostureDuration: Long
)
