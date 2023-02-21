package com.knocklock.presentation.lockscreen

import android.service.notification.StatusBarNotification
import androidx.lifecycle.ViewModel
import com.knocklock.presentation.lockscreen.service.ComposeScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * @Created by 김현국 2023/02/20
 */
@HiltViewModel
class LockScreenViewModel @Inject constructor() : ViewModel() {
    private val _notificationList = MutableStateFlow(emptyList<StatusBarNotification>())
    val notificationList = _notificationList.asStateFlow()

    private val _currentScreenState = MutableStateFlow<ComposeScreenState>(ComposeScreenState.LockScreen)
    val currentScreenState = _currentScreenState.asStateFlow()

    fun passActiveNotificationList(statusBarNotification: Array<StatusBarNotification>) {
        _notificationList.value = statusBarNotification.toMutableList()
    }

    fun updateScreenState(composeScreenState: ComposeScreenState) {
        _currentScreenState.value = composeScreenState
    }
}
