package com.knocklock.data.repository

import com.knocklock.data.mapper.toEntity
import com.knocklock.data.mapper.toModel
import com.knocklock.data.source.local.notification.dao.GroupDao
import com.knocklock.data.source.local.notification.dao.NotificationDao
import com.knocklock.domain.model.Group
import com.knocklock.domain.model.GroupWithNotification
import com.knocklock.domain.model.Notification
import com.knocklock.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override suspend fun insertNotifications(notification: Notification) {
        notificationDao.insertNotifications(
                notification.toEntity()
        )
    }

    /**
     * 모든 그룹과 그룹에 속한 Notification들을 가져오고 그룹의 속한 Notification들을 최근 시간으로 정렬합니다.
     *
     * 이후 그룹에 속한 Notification의 첫번째 값의 postedTime을 최근 시간순으로 Group을 정렬합니다.
     * @return Flow<List<[GroupWithNotification]>>
     */
    override fun getGroupWithNotificationsWithSorted(): Flow<List<GroupWithNotification>> {
        return groupDao.getGroupWithNotifications().map { groupWithNotifications ->
            groupWithNotifications.asSequence().filter { groupWithNotification ->
                groupWithNotification.notifications.isNotEmpty()
            }.map { groupWithNotification ->
                val notifications = groupWithNotification.notifications.sortedByDescending { it.postedTime }
                groupWithNotification.copy(
                    notifications = notifications
                ).toModel()
            }.toList().sortedByDescending { it.notifications[0].postedTime }
        }
    }

    override suspend fun removeNotificationsWithId(id: String) {
        notificationDao.deleteNotificationsWithId(id)
    }

    override suspend fun removeGroupWithNotifications(key: String) {
        groupDao.deleteGroupWithKey(key)
        notificationDao.deleteNotificationsWithGroupKey(key)
    }
}
