package com.knocklock.data.source.local.notification

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.knocklock.data.di.DatabaseModule.Companion.DB_NAME
import com.knocklock.data.source.local.notification.dao.GroupDao
import com.knocklock.data.source.local.notification.dao.NotificationDao
import com.knocklock.data.source.local.notification.entity.Group
import com.knocklock.data.source.local.notification.entity.Notification

/**
 * @Created by 김현국 2023/03/06
 */

@Database(entities = [Group::class, Notification::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): GroupDao
    abstract fun notificationDao(): NotificationDao
}
