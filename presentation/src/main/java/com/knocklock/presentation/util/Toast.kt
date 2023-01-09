package com.knocklock.presentation.util

import android.content.Context
import android.widget.Toast

/**
 * @Created by 김현국 2023/01/08
 * @Time 4:57 PM
 */

fun Context.showLongToastMessage(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}
fun Context.showShortToastMessage(message :String){
    Toast.makeText(this, message,Toast.LENGTH_SHORT).show()
}
