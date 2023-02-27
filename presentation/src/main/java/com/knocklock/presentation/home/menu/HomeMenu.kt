package com.knocklock.presentation.home.menu

import androidx.compose.ui.graphics.vector.ImageVector
import com.knocklock.presentation.icon.KnockLockIcons

enum class HomeMenu(val icon: ImageVector) {
    CLEAR(icon = KnockLockIcons.delete),
    SAVE(icon = KnockLockIcons.save),
    SETTING(icon = KnockLockIcons.settings),
    TMP(icon = KnockLockIcons.addBox)
}