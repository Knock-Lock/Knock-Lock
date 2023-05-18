package com.knocklock.data.mapper

import com.knocklock.data.source.local.lockscreen.LockScreenPreference
import com.knocklock.domain.model.LockScreen
import com.knocklock.domain.model.LockScreenBackground
import com.knocklock.domain.model.TimeFormat
import com.knocklock.data.source.local.lockscreen.LockScreenBackground as DataLockScreenBackground

fun LockScreenPreference.toDomain() = LockScreen(
    background = background.toDomain(),
    timeFormat = TimeFormat.getTimeFormat(timeFormat)
)

fun DataLockScreenBackground.toDomain(): LockScreenBackground {
    return when (this) {
        is DataLockScreenBackground.DefaultWallPaper -> {
            LockScreenBackground.DefaultWallPaper
        }
        is DataLockScreenBackground.LocalImage -> {
            LockScreenBackground.LocalImage(imageUri)
        }
    }
}

fun LockScreenBackground.toData(): DataLockScreenBackground {
    return when (this) {
        is LockScreenBackground.DefaultWallPaper -> {
            DataLockScreenBackground.DefaultWallPaper
        }
        is LockScreenBackground.LocalImage -> {
            DataLockScreenBackground.LocalImage(imageUri)
        }
    }
}