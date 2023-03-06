package com.knocklock.data.source.local.notification.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.knocklock.data.source.local.notification.entity.Group

/**
 * @Created by 김현국 2023/03/06
 */

@Dao
interface GroupDao {

    @Transaction
    @Query("SELECT * FROM `Group`")
    fun getGroupWithNotifications(): List<Group>

    @Insert
    fun insertGroup(group: Group)
}
