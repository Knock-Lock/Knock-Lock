package com.knocklock.data.mapper

import com.knocklock.data.source.local.notification.entity.Group
import com.knocklock.domain.model.Group as GroupModel

/**
 * @Created by 김현국 2023/03/06
 */

fun GroupModel.toEntity() = Group(
    key = this.key
)

fun Group.toModel() = GroupModel(
    key = this.key
)
