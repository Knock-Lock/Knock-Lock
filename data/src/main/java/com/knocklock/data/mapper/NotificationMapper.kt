package com.knocklock.data.mapper

import com.knocklock.data.source.local.notification.entity.Notification
import com.knocklock.domain.model.Notification as NotificationModel

/**
 * @Created by 김현국 2023/03/06
 */

fun NotificationModel.toEntity() = Notification(
    id = this.id,
    packageName = this.packageName,
    appTitle = this.appTitle,
    postedTime = this.postedTime,
    title = this.title,
    content = this.content,
    isClearable = this.isClearable,
    groupKey = this.groupKey,
    intent = this.intent,
)

fun Notification.toModel() = NotificationModel(
    id = this.id,
    packageName = this.packageName,
    appTitle = this.appTitle,
    postedTime = this.postedTime,
    title = this.title,
    content = this.content,
    isClearable = this.isClearable,
    groupKey = this.groupKey,
    intent = this.intent,
)
