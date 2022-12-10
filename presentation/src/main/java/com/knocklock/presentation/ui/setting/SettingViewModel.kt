package com.knocklock.presentation.ui.setting

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// TODO Hilt 세팅전 임시 세팅
//  DataStore나 SharedPreference에서 활성화 갑 가져와야함
class SettingViewModel : ViewModel() {

    val menuList: List<SettingMenu> = mutableListOf<SettingMenu>().apply {
        add(SettingMenu.SwitchMenu(MenuType.ACTIVATE_PASSWORD))
        add(SettingMenu.NormalMenu(MenuType.CHANGE_PASSWORD))
        add(SettingMenu.NormalMenu(MenuType.CREDIT))
    }

    private val _isPasswordActivated = MutableStateFlow<Boolean>(false)
    val isPasswordActivated = _isPasswordActivated.asStateFlow()

    // TODO 확장성 고려하여 스위치 메뉴 타입을 받아 분기
    //  현재 하나의 타입만 있어 임시 구현
    fun tmpChangeSwitchChecked(checked: Boolean) {
        _isPasswordActivated.value = checked
    }
}