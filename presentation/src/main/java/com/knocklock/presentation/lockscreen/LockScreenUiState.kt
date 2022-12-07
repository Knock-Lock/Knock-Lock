package com.knocklock.presentation.lockscreen

/**
 * @Created by 김현국 2022/12/07
 */

sealed class NotificationUiState {
    object Init : NotificationUiState()
    data class Success(val notificationList: List<Notification>) : NotificationUiState()
}
