package com.mobile.posturetracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PostureSessionDao {
    @Insert
    suspend fun insertSession(session: PostureSession)

    @Query("SELECT * FROM posture_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<PostureSession>>
}
