package com.knocklock.data.mapper

import com.knocklock.data.source.local.lockscreen.LockScreenPreference
import com.knocklock.domain.model.LockScreen
import com.knocklock.domain.model.LockScreenBackground
import com.knocklock.data.source.local.lockscreen.LockScreenBackground as DataLockScreenBackground

fun LockScreenPreference.toDomain() = LockScreen(
    background = background.toDomain()
)

fun DataLockScreenBackground.toDomain(): LockScreenBackground {
    return when (this) {
        is DataLockScreenBackground.DefaultWallPaper -> {
            LockScreenBackground.DefaultWallPaper
        }
        is DataLockScreenBackground.LocalImage -> {
            LockScreenBackground.LocalImage(imageRes)
        }
    }
}