package com.knocklock.presentation.home.menu

import androidx.annotation.StringRes
import com.knocklock.presentation.R

enum class HomeMenu(@StringRes val textRes: Int) {
    CLEAR(R.string.home_menu_clear),
    SAVE(R.string.home_menu_save),
    SETTING(R.string.setting),
    TMP(R.string.tmp)
}