package com.knocklock.data.source.local.notification.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Created by 김현국 2023/03/06
 */

@Entity
data class Notification(
    @PrimaryKey val id: String = "",
    val packageName: String = "",
    val appTitle: String = "",
    val postedTime: String = "",
    val title: String = "",
    val content: String = "",
    val isClearable: Boolean = false,
    val groupKey: String = ""
)
