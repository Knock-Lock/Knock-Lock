package com.knocklock.presentation.home.editcontent

import androidx.annotation.StringRes
import com.knocklock.presentation.R

enum class HomeEditType(
    @StringRes val labelRes: Int
) {
    TimeFormat(
        labelRes = R.string.home_edit_clock_type
    ),

    Background(
        labelRes = R.string.home_edit_background_type
    ),

    Unknown(
        labelRes = R.string.home_edit_unknown_type
    )
}