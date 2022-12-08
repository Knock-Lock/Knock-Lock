package com.knocklock.presentation.ui.setting

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingViewModel : ViewModel() {

    private val _isActivated = MutableStateFlow<Boolean>(false)
    val isActivated = _isActivated.asStateFlow()

    fun tmpChangeSwitchChecked(checked: Boolean) {
        _isActivated.value = checked
    }
}