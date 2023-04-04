package com.knocklock.domain.repository

import com.knocklock.domain.model.Group
import com.knocklock.domain.model.GroupWithNotification
import com.knocklock.domain.model.Notification
import kotlinx.coroutines.flow.Flow

/**
 * @Created by 김현국 2023/03/06
 */
interface NotificationRepository {

    suspend fun insertGroup(group: Group)

    suspend fun insertNotifications(notification: Notification)

    fun getGroupWithNotificationsWithSorted(): Flow<List<GroupWithNotification>>

    suspend fun removeNotificationsWithId(id: String)

    suspend fun removeGroupWithNotifications(key: String)
}
