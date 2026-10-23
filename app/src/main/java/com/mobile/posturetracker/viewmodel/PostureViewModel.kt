package com.mobile.posturetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mobile.posturetracker.data.PostureRepository
import com.mobile.posturetracker.data.PostureSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PostureViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostureRepository(application)
    val sessions: StateFlow<List<PostureSession>> = repository.getAllSessions()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addSession(session: PostureSession) {
        viewModelScope.launch {
            repository.insertSession(session)
        }
    }
}

