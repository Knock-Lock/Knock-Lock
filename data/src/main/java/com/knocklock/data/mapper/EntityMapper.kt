package com.knocklock.data.mapper

import com.knocklock.data.source.local.lockscreen.NotificationEntity
import com.knocklock.domain.model.Notification

/**
 * @Created by 김현국 2022/12/06
 * @Time 3:28 PM
 */

fun Notification.toEntity() = NotificationEntity(
    id = this.id,
    title = this.title,
    text = this.text,
    subText = this.subText
)

fun NotificationEntity.toModel() = Notification(
    id = this.id,
    title = this.title,
    text = this.text,
    subText = this.subText
)
