package com.knocklock.presentation.home.menu

import androidx.compose.ui.graphics.vector.ImageVector
import com.knocklock.presentation.icon.KnockLockIcons

enum class HomeMenu(val icon: ImageVector) {
    Clear(icon = KnockLockIcons.delete),
    Save(icon = KnockLockIcons.save),
    Settings(icon = KnockLockIcons.settings),
    Edit(icon = KnockLockIcons.addBox)
}