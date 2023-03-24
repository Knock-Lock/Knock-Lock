package com.knocklock.presentation.lockscreen

import android.content.pm.PackageManager
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knocklock.domain.model.User
import com.knocklock.domain.repository.NotificationRepository
import com.knocklock.domain.usecase.lockscreen.GetLockScreenUseCase
import com.knocklock.domain.usecase.setting.GetUserUseCase
import com.knocklock.presentation.lockscreen.mapper.toModel
import com.knocklock.presentation.lockscreen.model.LockScreen
import com.knocklock.presentation.lockscreen.model.LockScreenBackground
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @Created by 김현국 2023/03/15
 */

@HiltViewModel
class LockScreenViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val getUserUseCase: GetUserUseCase,
    private val getLockScreenUseCase: GetLockScreenUseCase
) : ViewModel() {

    private val _notificationList: MutableStateFlow<NotificationUiState> = MutableStateFlow(
        NotificationUiState.Empty
    )
    val notificationList = _notificationList.asStateFlow()

    private val _currentLockState: MutableStateFlow<User?> = MutableStateFlow(null)
    val currentLockState = _currentLockState.asStateFlow()

    private val _currentBackground: MutableStateFlow<LockScreen> = MutableStateFlow(
        LockScreen(
            LockScreenBackground.DefaultWallPaper
        )
    )
    val currentBackground = _currentBackground.asStateFlow()

    private val _composeScreenState = MutableStateFlow<ComposeScreenState>(ComposeScreenState.LockScreen)
    val composeScreenState = _composeScreenState.asStateFlow()

    init {
        getCurrentLockState()
        getCurrentLockScreenBackground()
    }

    fun getGroupNotifications(packageManager: PackageManager) {
        viewModelScope.launch {
            notificationRepository.getGroupWithNotificationsWithSorted().collect { groups ->
                _notificationList.value = NotificationUiState.Success(
                    groups.map { it.toModel(packageManager) }
                )
            }
        }
    }

    private fun getCurrentLockState() {
        viewModelScope.launch {
            getUserUseCase().collect { user ->
                _currentLockState.value = user
            }
        }
    }

    private fun getCurrentLockScreenBackground() {
        viewModelScope.launch {
            getLockScreenUseCase().collect { lockscreen ->
                _currentBackground.value = lockscreen.toModel()
            }
        }
    }

    fun setComposeScreenState(composeScreenState: ComposeScreenState) {
        _composeScreenState.value = composeScreenState
    }
}

sealed class ComposeScreenState {
    object LockScreen : ComposeScreenState()
    object PassWordScreen : ComposeScreenState()
}
