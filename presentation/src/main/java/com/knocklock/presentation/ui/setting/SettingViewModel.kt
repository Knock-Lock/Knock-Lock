package com.knocklock.presentation.ui.setting

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

// TODO Hilt 세팅전 임시 세팅
//  DataStore나 SharedPreference에서 활성화 갑 가져와야함
class SettingViewModel : ViewModel() {

    val userSetting = MutableStateFlow(
        UserSettings(isPasswordActivated = false)
    )

    fun onChangedPasswordActivated(checked: Boolean) {

    }
}

data class UserSettings(
    val isPasswordActivated: Boolean
)