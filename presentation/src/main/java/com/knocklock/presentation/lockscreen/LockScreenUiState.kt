package com.knocklock.presentation.lockscreen

import com.knocklock.presentation.lockscreen.model.GroupWithNotification

/**
 * @Created by 김현국 2022/12/07
 */

sealed class NotificationUiState {
    object Empty : NotificationUiState()
    data class Success(
        val groupWithNotification: List<GroupWithNotification>,
    ) : NotificationUiState()
}

data class NotificationUiFlagState(
    val expandable: Boolean = false,
    val clickable: Boolean = false,
)
