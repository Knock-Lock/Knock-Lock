package com.knocklock.data.source.local.notification.dao

import androidx.room.Dao
import androidx.room.Insert
import com.knocklock.data.source.local.notification.entity.Notification

/**
 * @Created by 김현국 2023/03/06
 */

@Dao
interface NotificationDao {

    @Insert
    fun insertNotifications(vararg notifications: Notification)
}