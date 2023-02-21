package com.knocklock.domain.model

data class LockScreen(
    val background: LockScreenBackground
)


sealed interface LockScreenBackground {
    object DefaultWallPaper : LockScreenBackground
    data class LocalImage(val imageRes: Int) : LockScreenBackground
}