package com.knocklock.data.source.local.notification.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.knocklock.data.source.local.notification.entity.Notification

/**
 * @Created by 김현국 2023/03/06
 */

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(vararg notifications: Notification)

    @Delete
    suspend fun deleteNotificationsWithIds(vararg notifications: Notification)

    @Query(
        "DELETE FROM notification WHERE groupKey = :groupKey",
    )
    suspend fun deleteNotificationsWithGroupKey(groupKey: String)
}
