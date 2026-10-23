package com.mobile.posturetracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PostureSession::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postureSessionDao(): PostureSessionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "posture_guardian_db"
            ).build().also { INSTANCE = it }
        }
    }
}
