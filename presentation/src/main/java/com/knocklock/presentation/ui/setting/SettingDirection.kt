package com.knocklock.presentation.ui.setting

interface Menu {
    val title: String
    val isNeedSwitch: Boolean
    val route: String
}

object ActivatePassword : Menu {
    override val title = "비밀번호 활성화"
    override val isNeedSwitch = true
    override val route = "activate_password"
}

object ChangePassword : Menu {
    override val title = "비밀번호 변경"
    override val isNeedSwitch = false
    override val route = "change_password"
}

object Credit : Menu {
    override val title = "크레딧"
    override val isNeedSwitch = false
    override val route = "credit"
}