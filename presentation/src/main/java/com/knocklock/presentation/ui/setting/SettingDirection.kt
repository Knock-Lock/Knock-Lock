package com.knocklock.presentation.ui.setting

interface SettingDirection {
    val route: String
}

object Setting : SettingDirection {
    override val route: String = "setting"
}

object Password : SettingDirection {
    override val route: String = "password"
}

object Credit : SettingDirection {
    override val route: String = "credit"
}