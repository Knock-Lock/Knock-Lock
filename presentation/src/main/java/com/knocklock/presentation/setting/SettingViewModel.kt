package com.knocklock.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knocklock.domain.model.AuthenticationType
import com.knocklock.domain.usecase.setting.ActivateLockUseCase
import com.knocklock.domain.usecase.setting.ChangeAuthenticationTypeUseCase
import com.knocklock.domain.usecase.setting.GetUserUseCase
import com.knocklock.domain.usecase.setting.UpdatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    getUserUseCase: GetUserUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val changeAuthenticationTypeUseCase: ChangeAuthenticationTypeUseCase,
    private val activateLockUseCase: ActivateLockUseCase
) : ViewModel() {

    val userSetting = getUserUseCase().map { user ->
        UserSettings(
            password = user.password,
            isPasswordActivated = user.authenticationType == AuthenticationType.PASSWORD,
            isLockActivated = user.isLockActivated,
        )
    }

    fun onPasswordActivatedChanged(checked: Boolean) {
        viewModelScope.launch {
            if (checked) {
                changeAuthenticationTypeUseCase(AuthenticationType.PASSWORD)
            } else {
                changeAuthenticationTypeUseCase(AuthenticationType.GESTURE)
            }
        }
    }

    fun onLockActivatedChanged(checked: Boolean) {
        viewModelScope.launch {
            activateLockUseCase(checked)
        }
    }
}

data class UserSettings(
    val password: String = "",
    val isPasswordActivated: Boolean = false,
    val isLockActivated: Boolean = false,
)