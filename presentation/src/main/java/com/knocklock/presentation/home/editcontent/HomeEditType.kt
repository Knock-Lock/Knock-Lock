package com.knocklock.presentation.home.editcontent

import androidx.annotation.StringRes
import com.knocklock.presentation.R

enum class HomeEditType(
    @StringRes val labelRes: Int
) {
    CLOCK(
        labelRes = R.string.home_edit_clock_type
    ),

    BACKGROUND(
        labelRes = R.string.home_edit_background_type
    ),

    Unknown(
        labelRes = R.string.home_edit_unknown_type
    )
}