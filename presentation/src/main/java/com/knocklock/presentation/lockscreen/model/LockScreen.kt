package com.knocklock.presentation.lockscreen.model

import androidx.compose.runtime.Immutable
import com.knocklock.domain.model.TimeFormat

/**
 * @Created by 김현국 2023/03/24
 */

@Immutable
data class LockScreen(
    val background: LockScreenBackground,
    val timeFormat: TimeFormat,
)

sealed interface LockScreenBackground {
    object DefaultWallPaper : LockScreenBackground
    data class LocalImage(val imageUri: String) : LockScreenBackground
}
