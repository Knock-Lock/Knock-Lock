package com.knocklock.presentation.lockscreen.mapper

import android.content.pm.PackageManager
import com.knocklock.presentation.lockscreen.model.GroupWithNotification
import com.knocklock.domain.model.GroupWithNotification as GroupWithNotificationModel

/**
 * @Created by 김현국 2023/03/07
 */

fun GroupWithNotificationModel.toModel(packageManager: PackageManager) = GroupWithNotification(
    group = this.group.toModel(),
    notifications = this.notifications.map { it.toModel(packageManager) }
)
