package com.knocklock.presentation.lockscreen.mapper

import com.knocklock.presentation.lockscreen.Notification
import com.knocklock.domain.model.Notification as NotificationModel

/**
 * @Created by 김현국 2023/02/24
 */

fun Notification.toModel() = NotificationModel(
    id = this.id,
    groupKey = this.id,
    appTitle = this.appTitle,
    postTime = this.notiTime,
    packageName = this.id,
    title = this.title,
    content = this.content,
    isClearable = this.isClearable,
    intent = this.intent.toString() ?: ""
)
