package com.knocklock.presentation.lockscreen.service

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.service.notification.StatusBarNotification
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.*
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.knocklock.presentation.lockscreen.LockScreenRoute
import com.knocklock.presentation.lockscreen.rememberLockScreenStateHolder
import com.knocklock.presentation.lockscreen.util.ComposeLifecycleOwner
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @Created by 김현국 2023/01/05
 * @Time 3:47 PM
 */
class InitLockScreenView(
    private val context: Context,
    private val composeView: ComposeView,
    private val onComposeViewListener: OnComposeViewListener,
    private val point: Point
) {
    private val lifecycleOwner by lazy { ComposeLifecycleOwner() }
    private val composeViewModelStore by lazy { ViewModelStore() }

    private val notificationList = MutableStateFlow(emptyList<StatusBarNotification>())

    init {
        createComposeLockScreenView()
    }

    private fun createComposeLockScreenView() {
        composeView.setContent {
            val stateHolder = rememberLockScreenStateHolder(context = context)
            val notificationListState by notificationList.collectAsState()

            LaunchedEffect(key1 = notificationListState) {
                stateHolder.updateNotificationList(notificationListState)
            }
            val notificationUiState by stateHolder.notificationList.collectAsState()

            LockScreenRoute(
                notificationUiState,
                userSwipe = {
                    onComposeViewListener.remove(composeView)
                },
                onRemoveNotification = { notifications ->
                    onComposeViewListener.removeNotifications(notifications)
                }
            )
        }
        lifecycleOwner.apply {
            performRestore(null)
            handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }
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

    fun getWindowManagerLayoutParams(): WindowManager.LayoutParams {
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
            PixelFormat.TRANSLUCENT
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

    fun passActiveNotificationList(statusBarNotification: Array<StatusBarNotification>) {
        notificationList.value = statusBarNotification.toList()
    }
}
interface OnComposeViewListener {
    fun remove(composeView: ComposeView)
    fun removeNotifications(keys: List<String>)
}
