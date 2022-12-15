package com.knocklock.presentation.lockscreen

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import com.knocklock.domain.usecase.notification.InsertNotificationUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import com.knocklock.domain.model.Notification as NotificationDomainModel

/**
 * @Created by 김현국 2022/12/04
 * @Time 5:50 PM
 */

@AndroidEntryPoint
class LockScreenNotificationListener : NotificationListenerService() {

    private val job by lazy { SupervisorJob() }
    private val scope by lazy { CoroutineScope(Dispatchers.IO + job) }

    @Inject lateinit var insertNotificationUseCase: InsertNotificationUseCase

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        val packageName = sbn?.packageName
        if (sbn != null && !TextUtils.isEmpty(packageName)) {
            val notification: Notification = sbn.notification
            val extras = notification.extras
            val title = extras.getString(Notification.EXTRA_TITLE).toString()
            val text = extras.getString(Notification.EXTRA_TEXT).toString()
            val subText = extras.getString(Notification.EXTRA_SUB_TEXT).toString()
            val smallIcon = notification.smallIcon
            val largeIcon = notification.getLargeIcon()

            if (title.isNotBlank() && text.isNotBlank()) {
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
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
}
