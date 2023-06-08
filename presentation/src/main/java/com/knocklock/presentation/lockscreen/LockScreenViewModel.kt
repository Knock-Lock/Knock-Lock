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
import com.knocklock.presentation.lockscreen.model.RemovedGroupNotification
import com.knocklock.presentation.lockscreen.util.toSnapShotStateMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    fun removeNotificationInDatabase(removedNotifications: RemovedGroupNotification) {
        viewModelScope.launch {
            notificationRepository.removeNotifications(*removedNotifications.toModel().notifications.toTypedArray())
        }
    }

    /**
     * 삭제할 Notifiation을 전달받아 viewModel에서 관리하는, [_recentNotificationList]와 [_recentNotificationUiFlagState]을 수정합니다.
     *
     * * [removedNotifications]에 담긴 키로 삭제를 할 [GroupWithNotification]레코드를 찾습니다.
     * * [removedNotifications]에 담긴 List<[Notification]>의 size가 찾은 [GroupWithNotification]의 notifications보다 크기가 크다면 전체삭제로 간주합니다.
     * * 전체삭제라면 Uiflag를 전부
     * @param removedNotifications 삭제할 그룹의 key와 List<Notification>이 담긴 data class입니다.
     */
    fun removeNotificationInState(removedNotifications: RemovedGroupNotification) {
        viewModelScope.launch {
            val filteredGroupNotification = _recentNotificationList.value.filter { groupWithNotification: GroupWithNotification ->
                groupWithNotification.group.key == removedNotifications.key
            }
            if (filteredGroupNotification.isEmpty()) {
                return@launch
            }

            val targetGroupNotification = filteredGroupNotification.map { groupWithNotification: GroupWithNotification -> groupWithNotification.copy() }[0]

            val targetNotificationsSize = targetGroupNotification.notifications.size
            val removedNotificationsSize = removedNotifications.removedNotifications.size
            if (targetNotificationsSize <= removedNotificationsSize) {
                _recentNotificationList.update { recentNotificationList ->
                    recentNotificationList.filter { groupWithNotification ->
                        groupWithNotification.group.key != removedNotifications.key
                    }
                }
                setRecentNotificationUiFlagMap(removedNotifications.key)
            } else {
                val updatedRecentNotificationList = _recentNotificationList.value.map { groupWithNotification: GroupWithNotification ->
                    if (groupWithNotification.group.key == targetGroupNotification.group.key) {
                        val targetNotifications = targetGroupNotification.notifications
                        val removedReflectedNotifications = targetNotifications - removedNotifications.removedNotifications.toSet()
                        if (removedReflectedNotifications.size == 1) {
                            updateRecentNotificationExpandable(targetGroupNotification.group.key)
                        }
                        groupWithNotification.copy(
                            notifications = removedReflectedNotifications,
                        )
                    } else {
                        groupWithNotification
                    }
                }

                _recentNotificationList.update {
                    updatedRecentNotificationList
                }
                setRecentNotificationUiFlagMap(removedNotifications.key)
            }
        }
    }

    /**
     * [_recentNotificationList]에서 전체 제거된 [GroupWithNotification]을 [_recentNotificationUiFlagState]에서도 제거합니다.
     */
    private fun removeRecentNotificationUiFlag(groupKey: String) {
        val removedNotificationUiFlagState = _recentNotificationUiFlagState.value.toMutableMap()
        removedNotificationUiFlagState.remove(groupKey)
        _recentNotificationUiFlagState.update { _ ->
            removedNotificationUiFlagState.toSnapShotStateMap()
        }
    }

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
    fun clearRecentNotificationList() {
        viewModelScope.launch {
            _recentNotificationList.update {
                emptyList()
            }
        }
    }

    fun setComposeScreenState(composeScreenState: ComposeScreenState) {
        _composeScreenState.value = composeScreenState
    }

    fun addRecentNotification(notification: NotificationModel, packageManager: PackageManager) {
        val isExisted = _recentNotificationList.value.any {
            it.group.key == notification.groupKey
        }

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
                0,
                GroupWithNotification(
                    group = Group(notification.groupKey),
                    notifications = notificationList,
                ),
            )
            _recentNotificationList.update {
                existedRecentGroupNotificationList
            }
        }
        setRecentNotificationUiFlagMap(notification.toModel(packageManager).groupKey)
    }

    /**
     * RecentNotificationUiFlagMap에 저장된 state(clickable)를 수정합니다.
     *
     * * RecentNotificationUiFlagMap[[key]]로 접근하여, 현재의 size를 가져와 2이상이면 clickable을 true로 설정합니다.
     * * 사이즈가 0이라면 Map에서 제거합니다.
     * * clickable이 true일 경우, LockScreenNotiItem의 expandable Icon이 보이게 됩니다.
     *
     * @param key 수정할 Notification이 속한 그룹의 key입니다.
     */
    private fun setRecentNotificationUiFlagMap(key: String) = with(_recentNotificationUiFlagState.value) {
        if (this.containsKey(key).not()) {
            this[key] = NotificationUiFlagState(clickable = false)
        } else {
            val currentNotificationList = _recentNotificationList.value.filter { groupWithNotification: GroupWithNotification -> groupWithNotification.group.key == key }

            val currentNotificationListSize = if (currentNotificationList.isNotEmpty()) {
                currentNotificationList[0].notifications.size
            } else {
                0
            }
            if (currentNotificationListSize == 0) {
                removeRecentNotificationUiFlag(key)
                return@with
            }

            this[key]?.let { uiFlag ->
                if (currentNotificationListSize >= 2) {
                    this[key] = uiFlag.copy(clickable = true)
                } else {
                    this[key] = uiFlag.copy(clickable = false)
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
    fun updateClickable(key: String, state: Boolean) {
//        with(_notificationUiFlagStateState.value) {
//        if (this.containsKey(key).not()) {
//            this[key] = NotificationUiFlagState()
//        } else {
//            this[key]?.let { uiFlag ->
//                this[key] = uiFlag.copy(clickable = state)
//            }
//        }
    }
}

sealed class ComposeScreenState {
    object LockScreen : ComposeScreenState()
    object PassWordScreen : ComposeScreenState()
}
