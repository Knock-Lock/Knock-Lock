package com.knocklock.presentation.lockscreen

/**
 * @Created by 김현국 2022/12/07
 */

sealed class NotificationUiState {
    object Empty : NotificationUiState()
    data class Success(
        val notificationList:
            List<Pair<Pair<String, String>, List<Notification>>>
    ) : NotificationUiState()
}
