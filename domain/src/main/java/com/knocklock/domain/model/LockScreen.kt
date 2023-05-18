package com.knocklock.domain.model

data class LockScreen(
    val background: LockScreenBackground = LockScreenBackground.DefaultWallPaper,
    val timeFormat: TimeFormat = TimeFormat.DEFAULT_TIME_FORMAT
)


sealed interface LockScreenBackground {
    object DefaultWallPaper : LockScreenBackground
    data class LocalImage(val imageUri: String) : LockScreenBackground
}