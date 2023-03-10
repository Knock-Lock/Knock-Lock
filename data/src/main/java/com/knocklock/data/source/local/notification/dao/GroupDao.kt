package com.knocklock.data.source.local.notification.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.knocklock.data.source.local.notification.entity.Group
import com.knocklock.data.source.local.notification.entity.GroupWithNotification
import kotlinx.coroutines.flow.Flow

/**
 * @Created by 김현국 2023/03/06
 */

@Dao
interface GroupDao {

    /**
     * 모든 그룹과 그룹에 속한 Notification들을 가져옵니다.
     * @return Flow<List<[GroupWithNotification]>>
     */
    @Transaction
    @Query("SELECT * FROM `Group`")
    fun getGroupWithNotifications(): Flow<List<GroupWithNotification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group)

    @Query(
        "DELETE FROM `GROUP` WHERE `key` Like :key"
    )
    suspend fun deleteGroupWithKey(key: String)
}
