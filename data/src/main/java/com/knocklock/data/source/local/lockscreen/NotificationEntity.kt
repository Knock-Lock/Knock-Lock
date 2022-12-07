package com.knocklock.data.source.local.lockscreen

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Created by 김현국 2022/12/06
 * @Time 2:27 PM
 */
@Entity(tableName = "notification_table")
data class NotificationEntity(

    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String?,
    val text: String?,
    val subText: String?
) {
    companion object {
        val EMPTY = NotificationEntity(
            id = 0,
            title = null,
            text = null,
            subText = null
        )
    }
}
