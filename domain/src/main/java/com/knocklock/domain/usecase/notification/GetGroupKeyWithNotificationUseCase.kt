package com.knocklock.domain.usecase.notification

import com.knocklock.domain.repository.NotificationRepository
import javax.inject.Inject

/**
 * @Created by 김현국 2023/02/24
 */
class GetGroupKeyWithNotificationUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    operator fun invoke() = notificationRepository.getGroupNotifications()
}
