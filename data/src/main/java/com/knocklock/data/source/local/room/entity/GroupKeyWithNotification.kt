package com.knocklock.data.source.local.room.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * @Created by 김현국 2023/02/24
 */
data class GroupKeyWithNotification(
    @Embedded val groupNotification: GroupNotification,
    @Relation(
        parentColumn = "groupKey",
        entityColumn = "groupKey"
    )
    val notifications: List<Notification>
)
