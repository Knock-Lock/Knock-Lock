package com.knocklock.data.source.local.lockscreen

import com.knocklock.domain.model.TimeFormat
import kotlinx.serialization.Serializable

@Serializable
data class LockScreenPreference(
    val background: LockScreenBackgroundPreference,
    val timeFormat: String
) {
    companion object {
        const val localPath = "lockscreen.preferences_pb"
        fun getDefaultInstance() = LockScreenPreference(
            background = LockScreenBackgroundPreference.DefaultWallPaper,
            timeFormat = TimeFormat.DEFAULT_TIME_FORMAT.name
        )
    }
}


@Serializable
sealed interface LockScreenBackgroundPreference {
    @Serializable
    object DefaultWallPaper : LockScreenBackgroundPreference

    @Serializable
    data class LocalImage(val imageUri: String) : LockScreenBackgroundPreference
}