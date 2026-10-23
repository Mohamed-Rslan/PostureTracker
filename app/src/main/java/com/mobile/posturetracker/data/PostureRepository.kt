package com.mobile.posturetracker.data

import android.content.Context
import kotlinx.coroutines.flow.Flow

class PostureRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val dao = db.postureSessionDao()

    fun getAllSessions(): Flow<List<PostureSession>> = dao.getAllSessions()
    suspend fun insertSession(session: PostureSession) = dao.insertSession(session)
}
