package com.knocklock.presentation.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knocklock.domain.model.AuthenticationType
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
    private val changeAuthenticationTypeUseCase: ChangeAuthenticationTypeUseCase
) : ViewModel() {

    val userSetting = getUserUseCase().map { user ->
        UserSettings(isPasswordActivated = user.authenticationType == AuthenticationType.PASSWORD)
    }

    fun onChangedPasswordActivated(checked: Boolean) {
        viewModelScope.launch {
            if (checked) {
                changeAuthenticationTypeUseCase(AuthenticationType.PASSWORD)
            } else {
                changeAuthenticationTypeUseCase(AuthenticationType.GESTURE)
            }
        }
    }
}

data class UserSettings(
    val isPasswordActivated: Boolean
)