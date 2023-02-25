package com.knocklock.domain.usecase.notification

import com.knocklock.domain.model.Notification
import com.knocklock.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * @Created by 김현국 2023/02/24
 */
class InsertNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(notification: Notification) = notificationRepository.insertNotification(notification)
}
