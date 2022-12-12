package com.knocklock.domain.usecase.notification

import com.knocklock.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * @Created by 김현국 2022/12/12
 * @Time 6:00 PM
 */
class DeleteAllNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke() {
        return notificationRepository.deleteAllNotification()
    }
}
