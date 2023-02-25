package com.knocklock.data.source.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * @Created by 김현국 2023/02/24
 */

@Entity(tableName = "groupNotifications")
data class GroupNotification(
    @PrimaryKey val groupKey: String,
)
