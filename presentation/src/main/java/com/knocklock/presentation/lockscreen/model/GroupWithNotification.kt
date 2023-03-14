package com.knocklock.presentation.lockscreen.model

/**
 * @Created by 김현국 2023/03/07
 */

data class GroupWithNotification(
    val group: Group,
    val notifications: List<Notification>
)
