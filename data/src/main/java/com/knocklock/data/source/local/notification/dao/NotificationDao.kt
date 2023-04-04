package com.knocklock.data.source.local.notification.dao

import androidx.room.Dao
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
    suspend fun insertNotifications(notifications: Notification)

    @Query(
        "DELETE FROM NOTIFICATION WHERE id = :id"
    )
    suspend fun deleteNotificationsWithId(id: String)

    @Query(
        "DELETE FROM NOTIFICATION WHERE groupKey = :groupKey"
    )
    suspend fun deleteNotificationsWithGroupKey(groupKey: String)
}
