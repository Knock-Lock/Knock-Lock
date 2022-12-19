package com.knocklock.presentation.password

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PasswordInputViewModel @Inject constructor(): ViewModel() {
    val inputPassword = mutableStateOf("")

    companion object {
        private const val MAX_PASSWORD_LENGTH = 6
    }

    fun onClickTextButton(text: String) {
        if (inputPassword.value.length >= MAX_PASSWORD_LENGTH) return
        inputPassword.value += text
    }

    fun onClickKeyboardAction(action: KeyboardAction) {
         when (action) {
             KeyboardAction.BACK_SPACE -> {
                 inputPassword.value = inputPassword.value.dropLast(1)
             }
         }
    }
}