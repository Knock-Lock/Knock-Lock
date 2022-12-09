package com.knocklock.data.repository

import android.content.Context
import com.knocklock.data.mapper.toEntity
import com.knocklock.data.mapper.toModel
import com.knocklock.data.source.local.AppDatabase
import com.knocklock.data.source.local.lockscreen.NotificationLocalDataSource
import com.knocklock.domain.model.Notification
import com.knocklock.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * @Created by 김현국 2022/12/06
 * @Time 2:17 PM
 */

class NotificationRepositoryImpl constructor(
    context: Context
) : NotificationRepository {

    private val notificationLocalDataSource = NotificationLocalDataSource(AppDatabase.getDatabase(context))

    override suspend fun insertNotification(notification: Notification) {
        notificationLocalDataSource.insertNotification(notificationEntity = notification.toEntity())
    }

    override suspend fun deleteAllNotification() {
        notificationLocalDataSource.deleteAllNotification()
    }

    override suspend fun deleteNotificationById(id: Int) {
        notificationLocalDataSource.deleteNotification(id = id)
    }

    override fun getNotificationList(): Flow<List<Notification>> {
        return notificationLocalDataSource.getNotificationList().map { list ->
            list.map {
                it.toModel()
            }
        }
    }
}
