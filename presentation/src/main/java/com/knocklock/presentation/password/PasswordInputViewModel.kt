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
        if (state.inputPassword.length >= MAX_PASSWORD_LENGTH) return

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
        if (state.inputPassword.isEmpty()) return

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
    val inputPassword: String

    data class PasswordNoneState(
        override val inputPassword: String
    ) : PasswordInputState

    data class PasswordConfirmState(
        override val inputPassword: String,
        val savedPassword: String,
        val mismatchPassword: Boolean = false
    ) : PasswordInputState
}