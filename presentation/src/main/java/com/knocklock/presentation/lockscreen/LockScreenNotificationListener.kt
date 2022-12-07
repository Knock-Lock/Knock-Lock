package com.knocklock.presentation.lockscreen

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import com.knocklock.data.repository.NotificationRepositoryImpl
import com.knocklock.domain.repository.NotificationRepository
import kotlinx.coroutines.*
import com.knocklock.domain.model.Notification as NotificationDomainModel

/**
 * @Created by 김현국 2022/12/04
 * @Time 5:50 PM
 */

class LockScreenNotificationListener : NotificationListenerService() {

    private val job by lazy { SupervisorJob() }
    private val scope by lazy { CoroutineScope(Dispatchers.IO + job) }
    private lateinit var notificationRepository: NotificationRepository
    override fun onCreate() {
        super.onCreate()
        notificationRepository = NotificationRepositoryImpl(this) // data layer 의존성 제거 hilt로
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val packageName = sbn?.packageName
        if (sbn != null && !TextUtils.isEmpty(packageName)) {
            val notification: Notification = sbn.notification
            val extras = notification.extras
            val title = extras.getString(Notification.EXTRA_TITLE)
            val text = extras.getString(Notification.EXTRA_TEXT)
            val subText = extras.getString(Notification.EXTRA_SUB_TEXT)
            val smallIcon = notification.smallIcon
            val largeIcon = notification.getLargeIcon()

            scope.launch {
                notificationRepository.insertNotification(
                    NotificationDomainModel(
                        id = 0,
                        title = title,
                        subText = subText,
                        text = text
                    )
                )
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}
