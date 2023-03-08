package com.knocklock.data.source.local.notification.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Created by 김현국 2023/03/06
 */
@Entity(tableName = "group")
data class Group(
    @PrimaryKey val key: String
)
