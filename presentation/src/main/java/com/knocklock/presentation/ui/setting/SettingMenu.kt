package com.knocklock.presentation.ui.setting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed class SettingMenu {

    data class SwitchMenu(
        val title: String,
        val isChecked: Boolean = false,
        val route: String,
        val onSwitchChanged: (Boolean) -> Unit
    ) : SettingMenu() {
        var checked by mutableStateOf(isChecked)
    }

    data class NormalMenu(
        val title: String,
        val route: String
    ) : SettingMenu()
}