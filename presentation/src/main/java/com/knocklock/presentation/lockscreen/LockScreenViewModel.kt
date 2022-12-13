package com.knocklock.presentation.lockscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knocklock.domain.usecase.notification.DeleteAllNotificationUseCase
import com.knocklock.domain.usecase.notification.GetNotificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @Created by 김현국 2022/12/06
 * @Time 4:07 PM
 */

@HiltViewModel
class LockScreenViewModel @Inject constructor(
    private val getNotificationUseCase: GetNotificationUseCase,
    private val deleteAllNotificationUseCase: DeleteAllNotificationUseCase
) : ViewModel() {

    private val _notificationList: MutableStateFlow<NotificationUiState> = MutableStateFlow(NotificationUiState.Empty)
    val notificationList = _notificationList.asStateFlow()

    init {
        deleteAllNotification()
        getNotificationList()
    }
    private fun getNotificationList() {
        viewModelScope.launch {
            getNotificationUseCase().collect { list ->
                val notificationList = list.map {
                    it.toModel()
                }
                _notificationList.value = NotificationUiState.Success(
                    notificationList = notificationList
                )
            }
        }
    }

    private fun deleteAllNotification() {
        viewModelScope.launch {
            deleteAllNotificationUseCase()
        }
    }
}
