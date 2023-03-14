package com.knocklock.data.source.local.notification.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database에 저장되는 Notification Group Entity입니다.
 *
 * Group Entity는 그룹화할 키를 가지고 있습니다.
 * * 형식은 packageName + appTitle + title(메시지 제목) 이거나
 * * packageName + appTitle + subText(단톡방의 경우, 단톡방의 이름)입니다.
 * * SubText가 없는 경우 : com.kakao.talk카카오톡김다큐
 * * SubText가 있는 경우 : com.kakao.talk카카오톡Mash-Up 13기 안드로이드
 * @property key Notification List들을 묶어줄 그룹키입니다.

 */
@Entity(tableName = "group")
data class Group(
    @PrimaryKey val key: String
)
