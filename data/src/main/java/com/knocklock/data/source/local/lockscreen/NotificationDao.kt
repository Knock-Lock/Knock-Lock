package com.knocklock.data.source.local.lockscreen

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * @Created by 김현국 2022/12/06
 * @Time 3:12 PM
 */
@Dao
interface NotificationDao {

    @Insert
    suspend fun insertNotification(entity: NotificationEntity)

    @Query("Delete from notification_table WHERE id LIKE :id")
    suspend fun deleteNotification(id: Int)

    @Query("Delete from notification_table")
    suspend fun deleteAllNotification()

    @Query("SELECT * FROM notification_table")
    fun getNotificationList(): Flow<List<NotificationEntity>>
}
