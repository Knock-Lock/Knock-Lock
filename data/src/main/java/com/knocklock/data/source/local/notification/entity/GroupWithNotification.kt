package com.knocklock.data.source.local.notification.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * @Created by 김현국 2023/03/06
 */
data class GroupWithNotification(
    @Embedded val group: Group,
    @Relation(
        parentColumn = "key",
        entityColumn = "groupKey"
    )
    val notifications: List<Notification>
)
