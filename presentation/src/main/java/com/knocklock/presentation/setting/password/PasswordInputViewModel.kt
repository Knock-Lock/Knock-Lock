package com.knocklock.presentation.setting.password

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knocklock.domain.usecase.setting.UpdatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordInputViewModel @Inject constructor(
    private val updatePasswordUseCase: UpdatePasswordUseCase
) : ViewModel() {
    var passwordInputState by mutableStateOf<PasswordInputState>(
        PasswordInputState.PasswordNoneState("")
    )

    private val _onSuccessUpdatePassword = MutableSharedFlow<Unit>()
    val onSuccessUpdatePassword: SharedFlow<Unit> = _onSuccessUpdatePassword

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

        if (state.inputPassword.length + 1 >= MAX_PASSWORD_LENGTH) {
            when (passwordInputState) {
                is PasswordInputState.PasswordNoneState -> {
                    checkPasswordNoneState(passwordInputState as PasswordInputState.PasswordNoneState)
                }
                is PasswordInputState.PasswordConfirmState -> {
                    checkPasswordConfirmState(passwordInputState as PasswordInputState.PasswordConfirmState)
                }
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

    private fun checkPasswordNoneState(state: PasswordInputState.PasswordNoneState) {
        passwordInputState = PasswordInputState.PasswordConfirmState(
            inputPassword = "",
            savedPassword = state.inputPassword
        )
    }

    private fun checkPasswordConfirmState(state: PasswordInputState.PasswordConfirmState) {
        passwordInputState = state.copy(isLoading = true)

        passwordInputState = if (state.inputPassword == state.savedPassword) {
            viewModelScope.launch {
                updatePasswordUseCase(state.savedPassword)
                _onSuccessUpdatePassword.emit(Unit)
            }
            state.copy(isLoading = false, mismatchPassword = false)
        } else {
            state.copy(
                inputPassword = "",
                mismatchPassword = true,
                isLoading = false
            )
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
        val mismatchPassword: Boolean = false,
        val isLoading: Boolean = false
    ) : PasswordInputState
}
