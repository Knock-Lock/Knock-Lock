package com.knocklock.domain.model

/**
 * @Created by 김현국 2023/03/06
 */
data class GroupWithNotification(
    val group: Group,
    val notifications: List<Notification>
)
