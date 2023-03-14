package com.knocklock.data.source.local.notification.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database에 저장되는 Notification Entity입니다.
 *
 * Notification Entity는 알림의 정보를 가집니다.

 * @property postedTime PrimaryKey인 알림이 온 시각입니다.
 * @property id StatusBarNotification의 id입니다.
 * @property appTitle Application의 이름입니다.
 * @property title Notification 제목입니다.
 * @property content Notification 내용입니다.
 * @property isClearable Notification의 삭제 가능 여부 입니다.
 * @property groupKey Notification이 속한 그룹의 key입니다.
 */

@Entity(tableName = "notification")
data class Notification(
    @PrimaryKey val postedTime: Long,
    val id: String = "",
    val packageName: String = "",
    val appTitle: String = "",
    val title: String = "",
    val content: String = "",
    val isClearable: Boolean = false,
    val groupKey: String = ""
)
