package com.knocklock.data.source.local.lockscreen

import kotlinx.serialization.Serializable

@Serializable
data class LockScreenPreference(
    val background: LockScreenBackground
) {
    companion object {
        const val localPath = "lockscreen.preferences_pb"
        fun getDefaultInstance() = LockScreenPreference(
            background = LockScreenBackground.ColorRes(colorRes = 0)
        )
    }
}


@Serializable
sealed interface LockScreenBackground {
    @Serializable
    data class ColorRes(val colorRes: Int) : LockScreenBackground
    @Serializable
    data class LocalImage(val imageUri: String) : LockScreenBackground
}