package com.knocklock.data.source.local.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Created by 김현국 2023/02/24
 */
@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "groupKey") val groupKey: String,
    @ColumnInfo(name = "appTitle") val appTitle: String,
    @ColumnInfo(name = "postTime") val postTime: String,
    @ColumnInfo(name = "packageName") val packageName: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "isClearable") val isClearable: Boolean,
    @ColumnInfo(name = "intent") val intent: String
)
