package com.knocklock.presentation.ui.setting

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.knocklock.presentation.R

sealed class SettingMenu {

    data class SwitchMenu(
        val type: MenuType,
        val isChecked: Boolean = false,
    ) : SettingMenu() {
        var checked by mutableStateOf(isChecked)
    }

    data class NormalMenu(
        val type: MenuType
    ) : SettingMenu()
}

enum class MenuType(
    @StringRes val titleRes: Int,
    @StringRes val routeRes: Int
) {
    ACTIVATE_PASSWORD(
        R.string.activate_password,
        R.string.activate_password_route
    ),
    CHANGE_PASSWORD(
        R.string.chagne_password,
        R.string.change_password_route
    ),
    CREDIT(
        R.string.credit,
        R.string.credit_route
    )
}