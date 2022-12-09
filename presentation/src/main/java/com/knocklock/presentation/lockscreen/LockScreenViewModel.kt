package com.knocklock.presentation.lockscreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knocklock.data.repository.NotificationRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * @Created by 김현국 2022/12/06
 * @Time 4:07 PM
 */

class LockScreenViewModel constructor(context: Context) : ViewModel() {

    private val notificationRepository = NotificationRepositoryImpl(context = context)

    private val _notificationList: MutableStateFlow<NotificationUiState> = MutableStateFlow(NotificationUiState.Empty)
    val notificationList = _notificationList.asStateFlow()

    init {
        deleteAllNotification()
        getNotificationList()
    }
    private fun getNotificationList() {
        viewModelScope.launch {
            notificationRepository.getNotificationList().collect { list ->
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
            notificationRepository.deleteAllNotification()
        }
    }
}
