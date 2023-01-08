package com.knocklock.presentation.lockscreen.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import com.knocklock.domain.usecase.notification.InsertNotificationUseCase
import com.knocklock.presentation.MainActivity
import com.knocklock.presentation.lockscreen.receiver.OnScreenEventListener
import com.knocklock.presentation.lockscreen.receiver.OnSystemBarEventListener
import com.knocklock.presentation.lockscreen.receiver.ScreenEventReceiver
import com.knocklock.presentation.lockscreen.receiver.SystemBarEventReceiver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import com.knocklock.domain.model.Notification as NotificationDomainModel

/**
 * @Created by 김현국 2022/12/04
 * @Time 5:50 PM
 */

@AndroidEntryPoint
class LockScreenNotificationListener :
    NotificationListenerService() {

    private val job by lazy { SupervisorJob() }
    private val scope by lazy { CoroutineScope(Dispatchers.IO + job) }

    private val windowManager by lazy { getSystemService(Context.WINDOW_SERVICE) as WindowManager }
    private val point by lazy { Point() }

    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    private val composeView by lazy { ComposeView(context = this) }
    private val initLockScreenView by lazy {
        InitLockScreenView(
            context = this,
            composeView = composeView,
            point = point,
            onComposeViewListener = object : OnComposeViewListener {
                override fun remove(composeView: ComposeView) {
                    windowManager.removeView(composeView)
                }
            }
        )
    }

    private val screenEventReceiver by lazy {
        ScreenEventReceiver(
            context = this,
            onScreenEventListener = object : OnScreenEventListener {
                override fun openLockScreenByIntent() {
                    addLockScreen()
                }
            }
        )
    }

    private val systemBarEventReceiver by lazy {
        SystemBarEventReceiver(
            context = this,
            onSystemBarEventListener = object : OnSystemBarEventListener {
                override fun onSystemBarClicked() {
                }
            }
        )
    }

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
                    insertNotificationUseCase(
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

    override fun onCreate() {
        super.onCreate()
        registerSystemBarEventReceiver()
        registerScreenEventReceiver()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            point.x = windowManager.maximumWindowMetrics.bounds.width()
            point.y = windowManager.maximumWindowMetrics.bounds.height()
        } else {
            windowManager.defaultDisplay.getRealSize(point)
        }
    }

    private fun addLockScreen() {
        val canOverlay = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 && !Settings.canDrawOverlays(this)

        if (!canOverlay) {
            requestScreenOverlay()
        }
        windowManager.addView(composeView, initLockScreenView.getWindowManagerLayoutParams())
    }

    private fun requestScreenOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            val builder = StringBuilder()
            builder.append("package:$packageName")
            val intent = Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse(builder.toString())).apply {
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
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            ANDROID_CHANNEL_ID,
            ANDROID_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
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
            PendingIntent.FLAG_IMMUTABLE
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
