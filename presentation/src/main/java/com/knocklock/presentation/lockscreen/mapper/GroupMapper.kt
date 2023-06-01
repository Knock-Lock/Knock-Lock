package com.knocklock.presentation.lockscreen.mapper

import com.knocklock.presentation.lockscreen.model.Group
import com.knocklock.domain.model.Group as GroupModel

/**
 * @Created by 김현국 2023/03/07
 */

fun GroupModel.toModel() = Group(
    key = this.key,
)

fun Group.toModel() = GroupModel(
    key = this.key,
)
