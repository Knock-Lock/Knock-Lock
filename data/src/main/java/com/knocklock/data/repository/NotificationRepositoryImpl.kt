package com.knocklock.data.repository

import androidx.room.withTransaction
import com.knocklock.data.mapper.toEntity
import com.knocklock.data.mapper.toModel
import com.knocklock.data.source.local.room.AppDatabase
import com.knocklock.data.source.local.room.entity.GroupNotification
import com.knocklock.domain.model.GroupKeyWithNotification
import com.knocklock.domain.model.Notification
import com.knocklock.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * @Created by 김현국 2023/02/24
 */
class NotificationRepositoryImpl @Inject constructor(
    private val appDatabase: AppDatabase
) : NotificationRepository {
    override fun getGroupNotifications(): List<GroupKeyWithNotification> {
        return appDatabase.groupKeyWithNotificationDao().getGroupNotifications().map { groupKeyWithNotification ->
            groupKeyWithNotification.toModel()
        }
    }

    override suspend fun insertGroupNotifications(groupKeyWithNotifications: List<GroupKeyWithNotification>) {
        appDatabase.withTransaction {
        }
    }

    override suspend fun insertNotification(notification: Notification) {
        with(appDatabase) {
            withTransaction {
                groupNotificationDao().insertGroupNotification(GroupNotification(groupKey = notification.groupKey))
                notificationDao().insertNotifications(notification.toEntity())
            }
        }
    }
}
