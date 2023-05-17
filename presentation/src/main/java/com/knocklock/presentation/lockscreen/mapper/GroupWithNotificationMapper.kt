package com.knocklock.presentation.lockscreen.mapper

import android.content.pm.PackageManager
import com.knocklock.domain.model.Group
import com.knocklock.presentation.lockscreen.model.GroupWithNotification
import com.knocklock.presentation.lockscreen.model.Notification
import com.knocklock.presentation.lockscreen.model.RemovedGroupNotification
import com.knocklock.domain.model.GroupWithNotification as GroupWithNotificationModel

/**
 * @Created by 김현국 2023/03/07
 */

fun GroupWithNotificationModel.toModel(packageManager: PackageManager) = GroupWithNotification(
    group = this.group.toModel(),
    notifications = this.notifications.map { it.toModel(packageManager) },
)

fun GroupWithNotification.toModel() = GroupWithNotificationModel(
    group = this.group.toModel(),
    notifications = this.notifications.map { notification: Notification ->
        notification.toModel()
    },
)

fun RemovedGroupNotification.toModel() = GroupWithNotificationModel(
    group = Group(this.key),
    notifications = this.removedNotifications.map { notification: Notification ->
        notification.toModel()
    },
)
