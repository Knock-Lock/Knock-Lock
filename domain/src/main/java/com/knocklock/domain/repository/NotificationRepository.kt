package com.knocklock.domain.repository

import com.knocklock.domain.model.Notification
import kotlinx.coroutines.flow.Flow

/**
 * @Created by 김현국 2022/12/06
 * @Time 2:19 PM
 */
interface NotificationRepository {
    suspend fun insertNotification(notification: Notification)

    suspend fun deleteAllNotification()

    suspend fun deleteNotificationById(id: Int)

    fun getNotificationList(): Flow<List<Notification>>
}
