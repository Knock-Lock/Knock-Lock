package com.knocklock.data.mapper

import com.knocklock.data.source.local.notification.entity.Group
import com.knocklock.data.source.local.notification.entity.GroupWithNotification
import com.knocklock.domain.model.Group as GroupModel
import com.knocklock.domain.model.GroupWithNotification as GroupWithNotificationModel

/**
 * @Created by 김현국 2023/03/06
 */

fun GroupModel.toEntity() = Group(
    key = this.key
)

fun Group.toModel() = GroupModel(
    key = this.key
)

fun GroupWithNotification.toModel() = GroupWithNotificationModel(
    group = this.group.toModel(),
    notifications = this.notifications.map { it.toModel() }
)
