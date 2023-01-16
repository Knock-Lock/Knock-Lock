package com.knocklock.presentation.lockscreen

import com.knocklock.domain.model.Notification as DomainNotification

/**
 * @Created by 김현국 2022/12/07
 */

fun DomainNotification.toModel() = Notification(
    id = this.id,
    drawable = null,
    appTitle = this.subText,
    notiTime = "Now",
    title = this.title,
    content = this.text
)
