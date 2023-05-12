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
import com.knocklock.presentation.lockscreen.model.Group
import com.knocklock.presentation.lockscreen.model.GroupWithNotification
import com.knocklock.presentation.lockscreen.model.LockScreen
import com.knocklock.presentation.lockscreen.model.LockScreenBackground
import com.knocklock.presentation.lockscreen.model.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Stack
import javax.inject.Inject
import com.knocklock.domain.model.Notification as NotificationModel

/**
 * @Created by 김현국 2023/03/15
 */

@HiltViewModel
class LockScreenViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    getUserUseCase: GetUserUseCase,
    getLockScreenUseCase: GetLockScreenUseCase,
) : ViewModel() {

    private val _oldNotificationList: MutableStateFlow<NotificationUiState> = MutableStateFlow(
        NotificationUiState.Empty,
    )
    val oldNotificationList = _oldNotificationList.asStateFlow()

    private val _recentNotificationList: MutableStateFlow<List<GroupWithNotification>> = MutableStateFlow(
        emptyList(),
    )

    val recentNotificationList = _recentNotificationList.asStateFlow()

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

    private val _oldNotificationUiFlagState: MutableStateFlow<SnapshotStateMap<String, NotificationUiFlagState>> = MutableStateFlow(
        SnapshotStateMap(),
    )

    val oldNotificationUiFlagState = _oldNotificationUiFlagState.asStateFlow()

    private val _recentNotificationUiFlagState: MutableStateFlow<SnapshotStateMap<String, NotificationUiFlagState>> = MutableStateFlow(
        SnapshotStateMap(),
    )
    val recentNotificationUiFlagState = _recentNotificationUiFlagState.asStateFlow()

    private val _composeScreenState = MutableStateFlow<ComposeScreenState>(ComposeScreenState.LockScreen)
    val composeScreenState = _composeScreenState.asStateFlow()

    fun getGroupNotifications(packageManager: PackageManager) {
        viewModelScope.launch {
            notificationRepository.getGroupWithNotificationsWithSorted().collect { groups ->
                _oldNotificationList.value = NotificationUiState.Success(
                    groups.map { it.toModel(packageManager) },
                )
                launch {
                    groups.forEach { groupNotification ->
                        launch {
                            val key = groupNotification.group.key
                            val flag = groupNotification.notifications.size >= 2
                            setOldNotificationUiFlagMap(key, flag)
                        }
                    }
                }
            }
        }
    }

    fun saveRecentNotificationToDatabase() {
        viewModelScope.launch {
            val stack = Stack<GroupWithNotification>()
            _recentNotificationList.value.forEach {
                stack.add(it)
            }
            while (stack.isNotEmpty()) {
                val groupWithNotificationStack = stack.pop()
                _recentNotificationList.update{
                    stack.toList()
                }
                notificationRepository.insertGroup(
                    groupWithNotificationStack.toModel().group,
                )
                notificationRepository.insertNotifications(
                    *groupWithNotificationStack.notifications.map { notification ->
                        notification.toModel()
                    }.toTypedArray(),
                )
            }
        }
    }

    fun setComposeScreenState(composeScreenState: ComposeScreenState) {
        _composeScreenState.value = composeScreenState
    }

    fun addRecentNotification(notification: NotificationModel, packageManager: PackageManager) {
        val isExisted = _recentNotificationList.value.count {
            it.group.key == notification.groupKey
        } == 1

        if (isExisted) {
            val existedGroupNotificationList = _recentNotificationList.value.map { groupWithNotification ->
                if (groupWithNotification.group.key == notification.groupKey) {
                    val recentNotification = groupWithNotification.notifications.toMutableList().apply {
                        add(notification.toModel(packageManager))
                    }
                    groupWithNotification.copy(
                        notifications = recentNotification,
                    )
                } else {
                    groupWithNotification
                }
            }
            val sortedRecentNotificationList = existedGroupNotificationList.map { groupWithNotification ->
                val notifications = groupWithNotification.notifications.sortedByDescending { it.postedTime }
                groupWithNotification.copy(
                    notifications = notifications,
                )
            }.sortedByDescending { it.notifications[0].postedTime }

            _recentNotificationList.update {
                sortedRecentNotificationList
            }
        } else {
            val existedRecentGroupNotificationList = _recentNotificationList.value.toMutableList()
            val notificationList = mutableListOf<Notification>().apply {
                add(notification.toModel(packageManager))
            }
            existedRecentGroupNotificationList.add(
                GroupWithNotification(
                    group = Group(notification.groupKey),
                    notifications = notificationList,
                ),
            )
            _recentNotificationList.update {
                existedRecentGroupNotificationList
            }
        }
        setRecentNotificationUiFlagMap(notification.toModel(packageManager))
    }

    private fun setRecentNotificationUiFlagMap(notification: Notification) = with(_recentNotificationUiFlagState.value) {
        val key = notification.groupKey
        if (this.containsKey(key).not()) {
            this[key] = NotificationUiFlagState(clickable = false)
        } else {
            val currentNotificationListSize = _recentNotificationList.value.filter { groupWithNotification: GroupWithNotification -> groupWithNotification.group.key == notification.groupKey }[0].notifications.size
            this[key]?.let { uiFlag ->
                if (currentNotificationListSize >= 2) {
                    this[key] = uiFlag.copy(clickable = true)
                }
            }
        }
    }
    private fun setOldNotificationUiFlagMap(key: String, flag: Boolean) = with(_oldNotificationUiFlagState.value) {
        if (this.containsKey(key).not()) {
            this[key] = NotificationUiFlagState(clickable = flag)
        } else {
            this[key]?.let { uiFlag ->
                this[key] = uiFlag.copy(clickable = flag)
            }
        }
    }

    fun updateOldNotificationExpandable(key: String) = with(_oldNotificationUiFlagState.value) {
        if (this.containsKey(key).not()) {
            this[key] = NotificationUiFlagState()
        } else {
            this[key]?.let { uiFlag ->
                this[key] = uiFlag.copy(expandable = uiFlag.expandable.not())
            }
        }
    }
    fun updateRecentNotificationExpandable(key: String) = with(_recentNotificationUiFlagState.value) {
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
