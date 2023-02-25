package com.knocklock.domain.repository

import com.knocklock.domain.model.GroupKeyWithNotification
import com.knocklock.domain.model.Notification

/**
 * @Created by 김현국 2023/02/24
 */
interface NotificationRepository {
    fun getGroupNotifications(): List<GroupKeyWithNotification>
    suspend fun insertGroupNotifications(groupKeyWithNotifications: List<GroupKeyWithNotification>)
    suspend fun insertNotification(notification: Notification)
}
