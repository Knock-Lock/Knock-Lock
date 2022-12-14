package com.knocklock.presentation.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knocklock.domain.model.AuthenticationType
import com.knocklock.domain.usecase.setting.ChangeAuthenticationTypeUseCase
import com.knocklock.domain.usecase.setting.GetUserTypeUseCase
import com.knocklock.domain.usecase.setting.UpdatePasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val getUserTypeUseCase: GetUserTypeUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val changeAuthenticationTypeUseCase: ChangeAuthenticationTypeUseCase
) : ViewModel() {

    val userSetting = MutableStateFlow(UserSettings(isPasswordActivated = false))

    init {
        getUserType()
    }

    private fun getUserType() {
        viewModelScope.launch {
            getUserTypeUseCase().collect { user ->
                if (user.authenticationType == AuthenticationType.PASSWORD) {
                    userSetting.value = UserSettings(isPasswordActivated = true)
                } else {
                    userSetting.value = UserSettings(isPasswordActivated = false)
                }
            }
        }
    }


    fun onChangedPasswordActivated(checked: Boolean) {
        viewModelScope.launch {
            changeAuthenticationTypeUseCase(checked)
        }
    }
}

data class UserSettings(
    val isPasswordActivated: Boolean
)