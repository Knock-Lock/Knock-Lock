package com.knocklock.presentation.ui.setting

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// TODO Hilt 세팅전 임시 세팅
//  DataStore나 SharedPreference에서 활성화 갑 가져와야함
class SettingViewModel : ViewModel() {

    private val _isActivated = MutableStateFlow<Boolean>(false)
    val isActivated = _isActivated.asStateFlow()

    fun tmpChangeSwitchChecked(checked: Boolean) {
        _isActivated.value = checked
    }
}