package com.knocklock.presentation.lockscreen

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.setViewTreeOnBackPressedDispatcherOwner
import androidx.activity.viewModels
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.compose.ui.platform.createLifecycleAwareWindowRecomposer
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.knocklock.presentation.lockscreen.model.RemovedGroupNotification
import com.knocklock.presentation.lockscreen.model.RemovedType.Old
import com.knocklock.presentation.lockscreen.model.RemovedType.Recent
import com.knocklock.presentation.lockscreen.receiver.NotificationPostedListener
import com.knocklock.presentation.lockscreen.receiver.NotificationPostedReceiver
import com.knocklock.presentation.lockscreen.service.LockScreenNotificationListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import com.knocklock.domain.model.Notification as NotificationModel

@AndroidEntryPoint
class LockScreenActivity : ComponentActivity() {

    private var composeView: ComposeView? = null

    private val window by lazy {
        this.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    private val lockScreenViewModel: LockScreenViewModel by viewModels()

    private val notificationPostedReceiver by lazy {
        NotificationPostedReceiver(
            this,
            onPostedNotificationPostedListener = object : NotificationPostedListener {
                override fun onPostedNotification(notification: String) {
                    val notificationModel: NotificationModel = Json.decodeFromString(notification)

                    lockScreenViewModel.addRecentNotification(notificationModel, packageManager)
                }
            },

        )
    }

    private val point by lazy { Point() }
    private var notificationListener: LockScreenNotificationListener? = null
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder) {
            val binder = service as LockScreenNotificationListener.LocalBinder
            notificationListener = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindowSize()
        registerNotificationPostedReceiver()
        composeView = ComposeView(this).apply {
            val parent = this.compositionContext
            setParentCompositionContext(parent)

            setContent {
                LockScreenHost(
                    onFinish = {
                        val copyList = lockScreenViewModel.recentNotificationList.value.map {
                            it.copy()
                        }
                        lockScreenViewModel.clearRecentNotificationList()
                        notificationListener?.saveRecentNotificationToDatabase(copyList)
                        this@LockScreenActivity.finish()
                    },
                    onRemoveNotifications = { removedGroupNotification: RemovedGroupNotification ->
                        when (removedGroupNotification.type) {
                            Recent -> {
                                lockScreenViewModel.removeNotificationInState(removedGroupNotification)
                            }
                            Old -> {
                                lockScreenViewModel.removeNotificationInDatabase(removedGroupNotification)
                            }
                        }
                    },
                    lockScreenViewModel = lockScreenViewModel,
                )
            }
            initViewTreeOwner(this)
        }
        window.addView(composeView, getWindowManagerLayoutParams())
    }

    override fun onStart() {
        super.onStart()
        Intent(this, LockScreenNotificationListener::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onStop() {
        super.onStop()
        unbindService(connection)
        mBound = false
    }

    override fun onDestroy() {
        super.onDestroy()
        composeView?.let {
            removeViewTreeOwner(it)
            windowManager.removeViewImmediate(it)
        }
        unregisterNotificationPostedReceiver()
        notificationListener = null
        composeView = null
    }

    @OptIn(ExperimentalComposeUiApi::class)
    private fun initViewTreeOwner(composeView: ComposeView) {
        composeView.apply {
            setViewTreeLifecycleOwner(this@LockScreenActivity)
            setViewTreeViewModelStoreOwner(this@LockScreenActivity)
            setViewTreeSavedStateRegistryOwner(this@LockScreenActivity)
            setViewTreeOnBackPressedDispatcherOwner(this@LockScreenActivity)
            compositionContext = createLifecycleAwareWindowRecomposer(lifecycle = lifecycle)
        }
    }

    private fun removeViewTreeOwner(composeView: ComposeView) {
        composeView.apply {
            compositionContext = null
            setViewTreeLifecycleOwner(null)
            setViewTreeViewModelStoreOwner(null)
            setViewTreeSavedStateRegistryOwner(null)
        }
    }

    private fun getWindowSize() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            point.x = window.maximumWindowMetrics.bounds.width()
            point.y = window.maximumWindowMetrics.bounds.height()
        } else {
            window.defaultDisplay.getRealSize(point)
        }
    }

    private fun getWindowManagerLayoutParams(): WindowManager.LayoutParams {
        val type: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
        } else {
            WindowManager.LayoutParams.TYPE_TOAST
        }

        val flags = (
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_FULLSCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN or
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )

        val params = WindowManager.LayoutParams(
            point.x,
            point.y,
            0,
            0,
            type,
            flags,
            PixelFormat.TRANSLUCENT,
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            params.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
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
    private fun registerNotificationPostedReceiver() {
        notificationPostedReceiver.registerReceiver()
    }
    private fun unregisterNotificationPostedReceiver() {
        notificationPostedReceiver.unregisterReceiver()
    }
}
