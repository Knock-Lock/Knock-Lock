package com.knocklock.presentation.lockscreen

import android.content.pm.PackageManager
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @Created by 김현국 2023/03/15
 */

@HiltViewModel
class LockScreenViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    getUserUseCase: GetUserUseCase,
    getLockScreenUseCase: GetLockScreenUseCase,
) : ViewModel() {

    private val _notificationList: MutableStateFlow<NotificationUiState> = MutableStateFlow(
        NotificationUiState.Empty,
    )
    val notificationList = _notificationList.asStateFlow()

    val currentLockState: StateFlow<User?> = getUserUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null,
    )

    val currentBackground: StateFlow<LockScreen> = getLockScreenUseCase()
        .map { lockScreen ->
            lockScreen.toModel()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            LockScreen(LockScreenBackground.DefaultWallPaper),
        )

    private val _composeScreenState = MutableStateFlow<ComposeScreenState>(ComposeScreenState.LockScreen)
    val composeScreenState = _composeScreenState.asStateFlow()

    fun getGroupNotifications(packageManager: PackageManager) {
        viewModelScope.launch {
            notificationRepository.getGroupWithNotificationsWithSorted().collect { groups ->
                _notificationList.value = NotificationUiState.Success(
                    groups.map { it.toModel(packageManager) },
                )
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
