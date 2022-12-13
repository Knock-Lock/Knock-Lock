package com.knocklock.domain.usecase.notification

import com.knocklock.domain.model.Notification
import com.knocklock.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * @Created by 김현국 2022/12/12
 * @Time 5:59 PM
 */
class InsertNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(notification: Notification) {
        return notificationRepository.insertNotification(notification = notification)
    }
}
