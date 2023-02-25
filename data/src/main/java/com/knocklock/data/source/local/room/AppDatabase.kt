package com.knocklock.data.source.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.knocklock.data.source.local.room.dao.GroupKeyWithNotificationDao
import com.knocklock.data.source.local.room.dao.GroupNotificationDao
import com.knocklock.data.source.local.room.dao.NotificationDao
import com.knocklock.data.source.local.room.entity.GroupNotification
import com.knocklock.data.source.local.room.entity.Notification

/**
 * @Created by 김현국 2023/02/24
 */
@Database(
    entities = [Notification::class, GroupNotification::class ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
    abstract fun groupNotificationDao(): GroupNotificationDao
    abstract fun groupKeyWithNotificationDao() : GroupKeyWithNotificationDao
}
