package com.knocklock.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.knocklock.data.source.local.room.entity.Notification

/**
 * @Created by 김현국 2023/02/24
 */

@Dao
interface NotificationDao {

    @Insert
    fun insertNotifications(vararg notifications: Notification)

    @Delete
    fun deleteNotifications(vararg notifications: Notification)

    @Query("SELECT * FROM notifications")
    fun getNotifications(): List<Notification>
}
