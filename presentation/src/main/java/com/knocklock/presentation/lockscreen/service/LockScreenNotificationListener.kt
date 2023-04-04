package com.knocklock.presentation.lockscreen.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import com.knocklock.domain.repository.NotificationRepository
import com.knocklock.presentation.MainActivity
import com.knocklock.presentation.lockscreen.LockScreenActivity
import com.knocklock.presentation.lockscreen.mapper.getDatabaseKey
import com.knocklock.presentation.lockscreen.mapper.isNotEmptyTitleOrContent
import com.knocklock.presentation.lockscreen.mapper.mapToList
import com.knocklock.presentation.lockscreen.mapper.toModel
import com.knocklock.presentation.lockscreen.model.Group
import com.knocklock.presentation.lockscreen.model.toModel
import com.knocklock.presentation.lockscreen.receiver.OnScreenEventListener
import com.knocklock.presentation.lockscreen.receiver.OnSystemBarEventListener
import com.knocklock.presentation.lockscreen.receiver.ScreenEventReceiver
import com.knocklock.presentation.lockscreen.receiver.SystemBarEventReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * @Created by 김현국 2022/12/04
 * @Time 5:50 PM
 */

@AndroidEntryPoint
class LockScreenNotificationListener :
    NotificationListenerService() {

    @Inject
    lateinit var notificationRepository: NotificationRepository

    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    private val job by lazy { SupervisorJob() }
    private val notificationScope by lazy { CoroutineScope(job + Dispatchers.Default) }

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): LockScreenNotificationListener = this@LockScreenNotificationListener
    }

    override fun onBind(intent: Intent): IBinder? {
        val action = intent.action
        return if (SERVICE_INTERFACE == action) {
            super.onBind(intent)
        } else {
            binder
        }
    }

    private val screenEventReceiver by lazy {
        ScreenEventReceiver(
            context = this,
            onScreenEventListener = object : OnScreenEventListener {
                override fun openLockScreenByIntent() {
                    addLockScreen()
                }
            },
        )
    }

    private val systemBarEventReceiver by lazy {
        SystemBarEventReceiver(
            context = this,
            onSystemBarEventListener = object : OnSystemBarEventListener {
                override fun onSystemBarClicked() {
                    // Todo 현재 PassWordScreen이 열려있는지
                }
            },
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val packageName = sbn?.packageName
        if (sbn != null && !TextUtils.isEmpty(packageName)) {
            notificationScope.launch {
                if (sbn.isNotEmptyTitleOrContent()) {
                    val notification = sbn.toModel(packageManager)
                    launch {
                        notificationRepository.insertGroup(
                            Group(
                                key = notification.groupKey,
                            ).toModel(),
                        )
                    }
                    launch {
                        notificationRepository.insertNotifications(notification)
                    }
                }
            }
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
        val packageName = sbn?.packageName
        if (sbn != null && !TextUtils.isEmpty(packageName)) {
            notificationScope.launch {
                notificationRepository.removeNotificationsWithId(sbn.key)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerSystemBarEventReceiver()
        registerScreenEventReceiver()
    }

    private fun initNotificationsToLockScreen() {
        notificationScope.launch {
            val copyActiveNotification = activeNotifications.toList().toTypedArray()
            copyActiveNotification.forEach { notification ->
                launch {
                    notification.getDatabaseKey(packageManager)?.let { key ->
                        notificationRepository.insertGroup(
                            Group(
                                key = key,
                            ).toModel(),
                        )
                    }
                }
            }
            mapToList(copyActiveNotification, packageManager).forEach { notification ->
                launch {
                    notificationRepository.insertNotifications(notification)
                }
            }
        }
    }

    private fun addLockScreen() {
        val canOverlay =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 && !Settings.canDrawOverlays(this)

        if (!canOverlay) {
            requestScreenOverlay()
        }
        initNotificationsToLockScreen()
        try {
            val intent = Intent(this, LockScreenActivity::class.java).apply {
                flags = (Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            }
            startActivity(intent)
        } catch (e: IllegalStateException) {
            Log.e("로그", "view is already added")
        }
    }

    private fun requestScreenOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            val builder = StringBuilder()
            builder.append("package:$packageName")
            val intent = Intent(
                "android.settings.action.MANAGE_OVERLAY_PERMISSION",
                Uri.parse(builder.toString()),
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            this.startActivity(intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(9999, createNotification())
        return START_REDELIVER_INTENT
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterSystemBarEventReceiver()
        unregisterScreenEventReceiver()
        job.cancel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            ANDROID_CHANNEL_ID,
            ANDROID_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val contentIntent = Intent(this, MainActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            contentIntent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(this, ANDROID_CHANNEL_ID)
            .setContentTitle("contentTitle")
            .setAutoCancel(true)
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun registerSystemBarEventReceiver() {
        systemBarEventReceiver.registerReceiver()
    }

    private fun unregisterSystemBarEventReceiver() {
        systemBarEventReceiver.unregisterReceiver()
    }

    private fun registerScreenEventReceiver() {
        screenEventReceiver.registerReceiver()
    }

    private fun unregisterScreenEventReceiver() {
        screenEventReceiver.unregisterReceiver()
    }

    companion object {
        private const val ANDROID_CHANNEL_ID = "KnockLockScreenNotification"
        private const val ANDROID_CHANNEL_NAME = "KnockLockScreen"
    }
}
