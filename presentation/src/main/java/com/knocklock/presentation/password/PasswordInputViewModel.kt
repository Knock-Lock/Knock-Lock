package com.knocklock.presentation.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PasswordInputViewModel @Inject constructor() : ViewModel() {
    var passwordInputState by mutableStateOf<PasswordInputState>(
        PasswordInputState.PasswordNoneState("")
    )

    companion object {
        private const val MAX_PASSWORD_LENGTH = 6
    }

    fun onClickTextButton(text: String) {
        addPassword(text)
    }

    fun onClickKeyboardAction(action: KeyboardAction) {
        when (action) {
            KeyboardAction.BACK_SPACE -> {
                removeLastPassword()
            }
        }
    }

    private fun addPassword(newPasswordNumber: String) {
        val state = passwordInputState
        if (state.getPasswordLength() >= MAX_PASSWORD_LENGTH) return

        passwordInputState = when (state) {
            is PasswordInputState.PasswordNoneState -> {
                state.copy(inputPassword = state.inputPassword + newPasswordNumber)
            }
            is PasswordInputState.PasswordConfirmState -> {
                state.copy(inputPassword = state.inputPassword + newPasswordNumber)
            }
        }
    }

    private fun removeLastPassword() {
        val state = passwordInputState
        if (state.getPasswordLength() <= 0) return

        passwordInputState = when (state) {
            is PasswordInputState.PasswordNoneState -> {
                state.copy(inputPassword = state.inputPassword.dropLast(1))
            }
            is PasswordInputState.PasswordConfirmState -> {
                state.copy(inputPassword = state.inputPassword.dropLast(1))
            }
        }
    }
}

sealed interface PasswordInputState {
    fun getPasswordLength(): Int

    data class PasswordNoneState(
        val inputPassword: String
    ) : PasswordInputState {
        override fun getPasswordLength() = inputPassword.length
    }

    data class PasswordConfirmState(
        val inputPassword: String,
        val savedPassword: String
    ) : PasswordInputState {
        override fun getPasswordLength() = inputPassword.length
    }
}