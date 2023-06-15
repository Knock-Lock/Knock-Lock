package com.knocklock.presentation.lockscreen.mapper

import com.knocklock.presentation.lockscreen.model.LockScreen
import com.knocklock.presentation.lockscreen.model.LockScreenBackground
import com.knocklock.domain.model.LockScreen as LockScreenModel
import com.knocklock.domain.model.LockScreenBackground as LockScreenBackgroundModel

/**
 * @Immutable 속성을 사용하기 위한 Domain to PresentationModel Mapper입니다.
 * @return [LockScreen]
 */
fun LockScreenModel.toModel() = LockScreen(
    background = this.background.toModel(),
    timeFormat = this.timeFormat,
)

fun LockScreenBackgroundModel.toModel() = when (this) {
    is LockScreenBackgroundModel.DefaultWallPaper -> {
        LockScreenBackground.DefaultWallPaper
    }
    is LockScreenBackgroundModel.LocalImage -> {
        LockScreenBackground.LocalImage(this.imageUri)
    }
}
