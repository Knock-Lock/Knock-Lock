package com.knocklock.data.source.local

import androidx.room.Database
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
        const val AppDatabaseName = "KnockLockDatabase"
    }
}
