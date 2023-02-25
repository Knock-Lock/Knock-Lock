package com.knocklock.data.mapper

import com.knocklock.data.source.local.room.entity.GroupKeyWithNotification
import com.knocklock.data.source.local.room.entity.Notification
import com.knocklock.domain.model.GroupKeyWithNotification as GroupKeyNotificationModel
import com.knocklock.domain.model.Notification as NotificationModel

/**
 * @Created by 김현국 2023/02/24
 */

fun GroupKeyWithNotification.toModel(): GroupKeyNotificationModel {
    return GroupKeyNotificationModel(
        groupKey = this.groupNotification.groupKey,
        notifications = this.notifications.map { notification ->
            notification.toModel()
        }
    )
}

fun Notification.toModel(): NotificationModel {
    return NotificationModel(
        id = this.id,
        groupKey = this.groupKey,
        appTitle = this.appTitle,
        postTime = this.postTime,
        packageName = this.packageName,
        title = this.title,
        content = this.content,
        isClearable = this.isClearable,
        intent = this.intent
    )
}

fun NotificationModel.toEntity() : Notification{
    return Notification(
        id = this.id,
        groupKey = this.groupKey,
        appTitle = this.appTitle,
        postTime = this.postTime,
        packageName = this.packageName,
        title = this.title,
        content = this.content,
        isClearable = this.isClearable,
        intent = this.intent
    )
}