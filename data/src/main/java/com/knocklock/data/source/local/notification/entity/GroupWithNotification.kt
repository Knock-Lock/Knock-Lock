package com.knocklock.data.source.local.notification.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Database에서 가져오는 [Group]과 Group에 속한 [Notification]들을 모은 data class입니다.
 *
 * Group.key 와 Notification.groupKey를 통해 1 : N 관계를 가집니다.

 * @property group Group key가 정의된 [Group] 객체입니다.
 * @property notifications Group key에 해당하는 List<[Notification]>입니다.
 */

data class GroupWithNotification(
    @Embedded val group: Group,
    @Relation(
        parentColumn = "key",
        entityColumn = "groupKey"
    )
    val notifications: List<Notification>
)
