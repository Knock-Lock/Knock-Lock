package com.knocklock.data.source.local.lockscreen

import com.knocklock.data.source.local.AppDatabase
import kotlinx.coroutines.flow.Flow

/**
 * @Created by 김현국 2022/12/06
 * @Time 2:26 PM
 */
class NotificationLocalDataSource constructor(
    private val appDatabase: AppDatabase
) {

    suspend fun insertNotification(notificationEntity: NotificationEntity) {
        appDatabase.notificationDao().insertNotification(entity = notificationEntity)
    }

    suspend fun deleteNotification(id: Int) {
        appDatabase.notificationDao().deleteNotification(id = id)
    }

    suspend fun deleteAllNotification() {
        appDatabase.notificationDao().deleteAllNotification()
    }

    fun getNotificationList(): Flow<List<NotificationEntity>> {
        return appDatabase.notificationDao().getNotificationList()
    }
}
