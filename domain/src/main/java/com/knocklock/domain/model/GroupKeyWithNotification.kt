package com.knocklock.domain.model

/**
 * @Created by 김현국 2023/02/24
 */
data class GroupKeyWithNotification(
    val groupKey : String,
    val notifications : List<Notification>
)