package com.knocklock.data.source.local.notification.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

/**
 * @Created by 김현국 2023/03/06
 */
@Entity
data class Group(
    @PrimaryKey val key: String
)
