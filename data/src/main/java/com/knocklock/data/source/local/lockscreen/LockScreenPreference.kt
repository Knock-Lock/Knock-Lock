package com.knocklock.data.source.local.lockscreen

import kotlinx.serialization.Serializable

@Serializable
data class LockScreenPreference(
    val background: LockScreenBackground
) {
    companion object {
        const val localPath = "lockscreen.preferences_pb"
        fun getDefaultInstance() = LockScreenPreference(
            background = LockScreenBackground.DefaultWallPaper
        )
    }
}


@Serializable
sealed interface LockScreenBackground {
    @Serializable
    object DefaultWallPaper : LockScreenBackground

    @Serializable
    data class LocalImage(val imageRes: Int) : LockScreenBackground
}