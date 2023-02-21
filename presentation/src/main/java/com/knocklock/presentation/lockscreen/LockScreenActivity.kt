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
import android.service.notification.StatusBarNotification
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.knocklock.domain.model.AuthenticationType
import com.knocklock.presentation.lockscreen.password.PassWordRoute
import com.knocklock.presentation.lockscreen.service.ComposeScreenState
import com.knocklock.presentation.lockscreen.service.LockScreenNotificationListener
import com.knocklock.presentation.lockscreen.util.ComposeLifecycleOwner
import com.knocklock.presentation.ui.theme.KnockLockTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class LockScreenActivity : ComponentActivity() {

    private val TAG = "로그"
    private val viewModel by viewModels<LockScreenViewModel>()
    private val manager by lazy { windowManager }
    private val composeView by lazy { ComposeView(this) }
    private val point by lazy { Point() }
    private val lifecycleOwner by lazy { ComposeLifecycleOwner() }
    private val composeViewModelStore by lazy { ViewModelStore() }
    private lateinit var notificationListener: LockScreenNotificationListener
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?) {
            val binder = service as LockScreenNotificationListener.NotificationListenerBinder
            notificationListener = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: a")
        setComposeView()
        setPoint()
        manager.addView(composeView, getWindowManagerLayoutParams())
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: a")
        EventBus.getDefault().register(this)
        Intent(this, LockScreenNotificationListener::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }

        if (mBound) {
            Log.d(TAG, "onStart: post")
            notificationListener.passNotificationWithEventBus()
        }
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: a")
        EventBus.getDefault().unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: a")
        unbindService(connection)
        mBound = false
    }

    private fun setPoint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            point.x = windowManager.maximumWindowMetrics.bounds.width()
            point.y = windowManager.maximumWindowMetrics.bounds.height()
        } else {
            windowManager.defaultDisplay.getRealSize(point)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPassedNotifications(notifications: Array<StatusBarNotification>) {
        Log.d(TAG, "onPassedNotifications: $notifications")
        viewModel.passActiveNotificationList(notifications)
    }

    private fun setComposeView() {
        composeView.setContent {
            KnockLockTheme {
                val screenState by viewModel.currentScreenState.collectAsState()
                val notificationListState by viewModel.notificationList.collectAsState()

                var startTransitionState by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier.fillMaxSize().background(color = Color.Blue)
                ) {
                    AnimatedVisibility(
                        visible = screenState == ComposeScreenState.LockScreen,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        val stateHolder = rememberLockScreenStateHolder(LocalContext.current)

                        val currentUserState by stateHolder.currentLockState.collectAsState()

                        LaunchedEffect(key1 = notificationListState) {
                            stateHolder.updateNotificationList(notificationListState)
                        }
                        val notificationUiState by stateHolder.notificationList.collectAsState()

                        LockScreenRoute(
                            modifier = Modifier,
                            notificationUiState,
                            userSwipe = {
                                currentUserState?.let { user ->
                                    when (user.authenticationType) {
                                        AuthenticationType.GESTURE -> {
                                            windowManager.removeView(composeView)
                                            onDestroy()
                                        }
                                        AuthenticationType.PASSWORD -> {
                                            viewModel.updateScreenState(ComposeScreenState.PassWordScreen)
                                        }
                                    }
                                }
                            },
                            onRemoveNotification = { notifications ->
                                if (mBound) {
                                    notificationListener.removeNotificationsWithScope(notifications)
                                }
                            },
                            startTransitionState = startTransitionState,
                            updateTransitionState = { state ->
                                startTransitionState = state
                            }
                        )
                    }

                    AnimatedVisibility(
                        visible = screenState == ComposeScreenState.PassWordScreen,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        PassWordRoute(
                            unLockPassWordScreen = {
                                windowManager.removeView(composeView)
                                onDestroy()
                            },
                            returnLockScreen = {
                                viewModel.updateScreenState(ComposeScreenState.LockScreen)
                            }
                        )
                    }
                }
            }
        }
        initComposeView()
    }

    private fun initComposeView() {
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
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
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
}
