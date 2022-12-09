package com.knocklock.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.knocklock.data.source.local.lockscreen.NotificationDao
import com.knocklock.data.source.local.lockscreen.NotificationEntity

/**
 * @Created by 김현국 2022/12/06
 * @Time 3:15 PM
 */

@Database(entities = [NotificationEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val appDatabase = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java,
                    "KnockLockDatabase"
                ).build()
                INSTANCE = appDatabase
                return appDatabase
            }
        }
    }
}
