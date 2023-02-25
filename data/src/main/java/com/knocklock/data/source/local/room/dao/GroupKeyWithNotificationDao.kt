package com.knocklock.data.source.local.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.knocklock.data.source.local.room.entity.GroupKeyWithNotification

/**
 * @Created by 김현국 2023/02/24
 */

@Dao
interface GroupKeyWithNotificationDao {

    @Transaction
    @Query("SELECT * FROM groupNotifications")
    fun getGroupNotifications(): List<GroupKeyWithNotification>
}
