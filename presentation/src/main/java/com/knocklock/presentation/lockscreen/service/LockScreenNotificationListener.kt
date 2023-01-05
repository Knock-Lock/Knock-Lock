package com.knocklock.presentation.lockscreen.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.knocklock.domain.usecase.notification.InsertNotificationUseCase
import com.knocklock.presentation.MainActivity
import com.knocklock.presentation.lockscreen.*
import com.knocklock.presentation.lockscreen.receiver.HomeWatcher
import com.knocklock.presentation.lockscreen.receiver.OpenLockScreen
import com.knocklock.presentation.lockscreen.receiver.StartApplicationReceiver
import com.knocklock.presentation.lockscreen.util.ComposeLifecycleOwner
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
    NotificationListenerService(),
    OpenLockScreen,
    HomeWatcher.OnSystemBarPressedListener {

    private val job by lazy { SupervisorJob() }
    private val scope by lazy { CoroutineScope(Dispatchers.IO + job) }

    private val windowManager by lazy { getSystemService(Context.WINDOW_SERVICE) as WindowManager }
    private val point by lazy { Point() }

    private val lifecycleOwner by lazy { ComposeLifecycleOwner() }
    private val composeViewModelStore by lazy { ViewModelStore() }
    private val composeView by lazy { ComposeView(this) }

    // screenLock Receiver
    private val startApplicationReceiver by lazy { StartApplicationReceiver(this) }
    private val notificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    // homeKeyWatcher
    private val homeWatcherReceiver by lazy { HomeWatcher(this) }

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
        registerStartApplicationReceiver()
        windowManager.defaultDisplay.getRealSize(point)
        addLockScreen()
    }

    private fun addLockScreen() {
        val canOverlay = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1 && !Settings.canDrawOverlays(this)

        if (!canOverlay) {
            requestScreenOverlay()
        }

        // 윈도우 등록
        createComposeLockScreenView()
        windowManager.addView(composeView, getWindowManagerLayoutParams())
    }

    private fun getWindowManagerLayoutParams(): LayoutParams {
        val type: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LayoutParams.TYPE_APPLICATION_OVERLAY
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            LayoutParams.TYPE_SYSTEM_ERROR
        } else {
            LayoutParams.TYPE_TOAST
        }

        val flags = (
            LayoutParams.FLAG_NOT_TOUCH_MODAL or
                LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                LayoutParams.FLAG_FULLSCREEN or
                LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                LayoutParams.FLAG_DISMISS_KEYGUARD or
                LayoutParams.FLAG_LAYOUT_IN_OVERSCAN or
                LayoutParams.FLAG_TRANSLUCENT_STATUS or
                LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )

        val params = LayoutParams(
            point.x,
            point.y,
            0,
            0,
            type,
            flags,
            PixelFormat.TRANSLUCENT
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // window에 ComposeView 등록시 상태바 영역까지 윈도우를 넓힘
            params.layoutInDisplayCutoutMode =
                LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES // 1
        }

        params.gravity = Gravity.TOP or Gravity.START
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        params.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            )
        return params
    }

    private fun requestScreenOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            val builder = StringBuilder()
            builder.append("package:")
            builder.append(this.packageName)
            val intent = Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse(builder.toString()))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            this.startActivity(intent)
        }
    }

    private fun createComposeLockScreenView() {
        initHomeWatcher()
        composeView.setContent {
            val stateHolder = rememberLockScreenStateHolder(context = this)
            val notificationUiState by stateHolder.notificationList.collectAsState()
            LockScreenRoute(notificationUiState, userSwipe = {
                windowManager.removeView(composeView)
            })
        }

        lifecycleOwner.performRestore(null)
        lifecycleOwner.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        ViewTreeLifecycleOwner.set(composeView, lifecycleOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)

        ViewTreeViewModelStoreOwner.set(composeView) { composeViewModelStore }
        val coroutineContext = AndroidUiDispatcher.CurrentThread
        val runRecomposeScope = CoroutineScope(coroutineContext)
        val recomposer = Recomposer(coroutineContext)
        composeView.compositionContext = recomposer
        runRecomposeScope.launch {
            recomposer.runRecomposeAndApplyChanges()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(9999, createNotification())
        return START_REDELIVER_INTENT
    }

    override fun open() {
        addLockScreen()
    }

    private fun registerStartApplicationReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_BOOT_COMPLETED)
        }
        registerReceiver(this.startApplicationReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeHomeWatcher()
        unregisterReceiver(this.startApplicationReceiver)
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

    override fun onSystemBarClicked() {
        /*
        todo 만약 잠김 상태라면? Password 스크린으로 이동
         아니라면 LockScreen해제
         */
    }

    private fun initHomeWatcher() {
        homeWatcherReceiver.setOnHomePressedListener(this)
        homeWatcherReceiver.startWatch()
    }

    private fun removeHomeWatcher() {
        homeWatcherReceiver.stopWatch()
    }

    companion object {
        private const val ANDROID_CHANNEL_ID = "KnockLockScreenNotification"
        private const val ANDROID_CHANNEL_NAME = "KnockLockScreen"
    }
}
