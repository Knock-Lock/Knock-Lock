package com.knocklock.presentation.lockscreen.model

import androidx.compose.runtime.Immutable

/**
 * @Created by 김현국 2023/03/24
 */

@Immutable
data class LockScreen(
    val background: LockScreenBackground
)

sealed interface LockScreenBackground {
    object DefaultWallPaper : LockScreenBackground
    data class LocalImage(val imageUri: String) : LockScreenBackground
}
