package com.knocklock.domain.usecase.notification

import com.knocklock.domain.model.Notification
import com.knocklock.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @Created by 김현국 2022/12/12
 * @Time 5:54 PM
 */
class GetNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke(): Flow<List<Notification>> {
        return notificationRepository.getNotificationList()
    }
}
