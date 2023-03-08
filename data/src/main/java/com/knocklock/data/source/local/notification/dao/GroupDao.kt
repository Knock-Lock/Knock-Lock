package com.knocklock.data.source.local.notification.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.knocklock.data.source.local.notification.entity.Group
import com.knocklock.data.source.local.notification.entity.GroupWithNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * @Created by 김현국 2023/03/06
 */

@Dao
interface GroupDao {

    @Transaction
    @Query("SELECT * FROM `Group`")
    fun getGroupWithNotifications(): Flow<List<GroupWithNotification>>

    @Transaction
    @Query("")
    fun getGroupWithNotificationsWithSorted(): Flow<List<GroupWithNotification>> {
        return flow {
            getGroupWithNotifications().collect { groupWithNotifications ->

                emit(
                    groupWithNotifications.filter {
                        it.notifications.isNotEmpty()
                    }.map { groupWithNotification ->
                        val notifications = groupWithNotification.notifications.sortedByDescending { it.postedTime }
                        groupWithNotification.copy(
                            notifications = notifications
                        )
                    }.sortedByDescending { it.notifications[0].postedTime }
                )
            }
        }.flowOn(Dispatchers.IO)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: Group)

    @Query(
        "DELETE FROM `GROUP` WHERE `key` Like :key"
    )
    suspend fun deleteGroupWithKey(key: String)
}
