package com.knocklock.presentation.lockscreen.model

import androidx.compose.runtime.Immutable

/**
 * @Created by 김현국 2023/03/07
 */

@Immutable
data class GroupWithNotification(
    val group: Group,
    val notifications: List<Notification>
)
