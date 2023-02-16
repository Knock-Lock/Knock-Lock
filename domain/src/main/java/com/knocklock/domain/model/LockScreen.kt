package com.knocklock.domain.model

data class LockScreen(
    val background: LockScreenBackground
)


sealed interface LockScreenBackground {
    data class ColorRes(val colorRes: Int) : LockScreenBackground
    data class LocalImage(val imageUri: String) : LockScreenBackground
}