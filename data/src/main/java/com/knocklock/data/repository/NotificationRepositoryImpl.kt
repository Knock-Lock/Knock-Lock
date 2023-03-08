package com.knocklock.data.repository

import com.knocklock.data.mapper.toEntity
import com.knocklock.data.mapper.toModel
import com.knocklock.data.source.local.notification.dao.GroupDao
import com.knocklock.data.source.local.notification.dao.NotificationDao
import com.knocklock.domain.model.Group
import com.knocklock.domain.model.GroupWithNotification
import com.knocklock.domain.model.Notification
import com.knocklock.domain.repository.NotificationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * @Created by 김현국 2023/03/06
 */
class NotificationRepositoryImpl @Inject constructor(
    private val groupDao: GroupDao,
    private val notificationDao: NotificationDao
) : NotificationRepository {
    override suspend fun insertGroup(group: Group) {
        groupDao.insertGroup(group = group.toEntity())
    }

    override suspend fun insertNotifications(vararg notifications: Notification) {
        withContext(Dispatchers.IO) {
            notificationDao.insertNotifications(
                *notifications.map { notification ->
                    notification.toEntity()
                }.toTypedArray()
            )
        }
    }

    override fun getGroupWithNotifications(): Flow<List<GroupWithNotification>> {
        return flow {
            groupDao.getGroupWithNotificationsWithSorted().collect { groups ->
                emit(groups.map { groupWithNotification -> groupWithNotification.toModel() })
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun removeNotificationsWithId(vararg ids: String) {
        notificationDao.deleteNotificationsWithIds(*ids)
    }

    override suspend fun removeGroupWithNotifications(key: String) {
        groupDao.deleteGroupWithKey(key)
        notificationDao.deleteNotificationsWithGroupKey(key)
    }
}
