package com.knocklock.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.knocklock.data.source.local.room.entity.GroupNotification

/**
 * @Created by 김현국 2023/02/24
 */

@Dao
interface GroupNotificationDao {
    @Query("SELECT * FROM groupNotifications")
    fun getAllGroupNotifications(): List<GroupNotification>

    @Insert
    fun insertGroupNotification(groupNotification: GroupNotification)
}
