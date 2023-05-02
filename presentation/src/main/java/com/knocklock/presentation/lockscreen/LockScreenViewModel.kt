package com.knocklock.presentation.lockscreen

import android.content.pm.PackageManager
import androidx.compose.runtime.snapshots.SnapshotStateMap
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

    private val _notificationUiFlagStateState: MutableStateFlow<SnapshotStateMap<String, NotificationUiFlagState>> = MutableStateFlow(
        SnapshotStateMap(),
    )

    val notificationUiFlagState = _notificationUiFlagStateState.asStateFlow()

    private val _composeScreenState = MutableStateFlow<ComposeScreenState>(ComposeScreenState.LockScreen)
    val composeScreenState = _composeScreenState.asStateFlow()

    fun getGroupNotifications(packageManager: PackageManager) {
        viewModelScope.launch {
            notificationRepository.getGroupWithNotificationsWithSorted().collect { groups ->
                _notificationList.value = NotificationUiState.Success(
                    groups.map { it.toModel(packageManager) },
                )
                launch {
                    groups.forEach { groupNotification ->
                        launch {
                            val key = groupNotification.group.key
                            val flag = groupNotification.notifications.size >= 2
                            initUiFlagMap(key, flag)
                        }
                    }
                }
            }
        }
    }

    fun setComposeScreenState(composeScreenState: ComposeScreenState) {
        _composeScreenState.value = composeScreenState
    }

    private fun initUiFlagMap(key: String, flag: Boolean) = with(_notificationUiFlagStateState.value) {
        if (this.containsKey(key).not()) {
            this[key] = NotificationUiFlagState(clickable = flag)
        } else {
            this[key]?.let { uiFlag ->
                this[key] = uiFlag.copy(clickable = flag)
            }
        }
    }

    fun updateExpandable(key: String) = with(_notificationUiFlagStateState.value) {
        if (this.containsKey(key).not()) {
            this[key] = NotificationUiFlagState()
        } else {
            this[key]?.let { uiFlag ->
                this[key] = uiFlag.copy(expandable = uiFlag.expandable.not())
            }
        }
    }
}

sealed class ComposeScreenState {
    object LockScreen : ComposeScreenState()
    object PassWordScreen : ComposeScreenState()
}
