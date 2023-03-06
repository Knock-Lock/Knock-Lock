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
import android.provider.Settings
import android.service.notification.StatusBarNotification
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.compositionContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.knocklock.domain.model.LockScreenBackground
import com.knocklock.presentation.R
import com.knocklock.presentation.lockscreen.service.ComposeScreenState
import com.knocklock.presentation.lockscreen.service.TestListener
import com.knocklock.presentation.lockscreen.util.ComposeLifecycleOwner
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LockScreenActivity : ComponentActivity(), TestListener.BindServiceCallBack {

    private val point by lazy { Point() }
    private val composeScreenState = MutableStateFlow<ComposeScreenState>(ComposeScreenState.LockScreen)

    lateinit var lockScreenService: TestListener
    var isConnectedService = false

    private val viewModel: LockScreenViewModel by viewModels()

    private val lifecycleOwner by lazy { ComposeLifecycleOwner() }

    private val composeView by lazy { ComposeView(context = this) }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            val bind: TestListener.LockScreenServiceBinder = binder as TestListener.LockScreenServiceBinder
            isConnectedService = true
            lockScreenService = bind.getService()
            lockScreenService.setBindServiceCallback(this@LockScreenActivity)

        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            println("로그 Disconnected")
            isConnectedService = false
        }
    }

    override fun passActiveNotificationList(statusBarNotification: Array<StatusBarNotification>) {
        statusBarNotification.forEach {
            println(it)
        }
        println("로그 in")
    }

    override fun onStart() {
        super.onStart()
        serviceBind()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            point.x = windowManager.maximumWindowMetrics.bounds.width()
            point.y = windowManager.maximumWindowMetrics.bounds.height()
        } else {
            windowManager.defaultDisplay.getRealSize(point)
        }
        composeView.setContent {
            val screenState by composeScreenState.collectAsState()
            var startTransitionState by remember { mutableStateOf(false) }
//            val stateHolder = rememberLockScreenStateHolder(LocalContext.current)
            val backgroundState by viewModel.currentBackground.collectAsState()

            val request = ImageRequest.Builder(LocalContext.current)
                .allowHardware(false)
                .data(
                    data = when (backgroundState.background) {
                        is LockScreenBackground.DefaultWallPaper -> {
                            R.drawable.default_wallpaper
                        }
                        is LockScreenBackground.LocalImage -> {
                            (backgroundState.background as LockScreenBackground.LocalImage).imageUri
                        }
                    }
                ).build()

            val imagePainter = rememberAsyncImagePainter(
                model = request
            )

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = imagePainter,
                    contentScale = ContentScale.FillBounds,
                    contentDescription = null
                )
//                AnimatedVisibility(
//                    visible = screenState == ComposeScreenState.LockScreen,
//                    enter = fadeIn(),
//                    exit = fadeOut()
//                ) {
//                    val notificationListState by notificationList.collectAsState()
//                    val currentUserState by stateHolder.currentLockState.collectAsState()
//
//                    LaunchedEffect(key1 = notificationListState) {
//                        stateHolder.updateNotificationList(notificationListState)
//                    }
//                    val notificationUiState by stateHolder.notificationList.collectAsState()
//
//                    LockScreenRoute(
//                        modifier = Modifier,
//                        notificationUiState,
//                        userSwipe = {
//                            currentUserState?.let { user ->
//                                when (user.authenticationType) {
//                                    AuthenticationType.GESTURE -> {
//                                        onComposeViewListener.remove()
//                                    }
//                                    AuthenticationType.PASSWORD -> {
//                                        composeScreenState.value = ComposeScreenState.PassWordScreen
//                                    }
//                                }
//                            }
//                        },
//                        onRemoveNotification = { notifications ->
//                            onComposeViewListener.removeNotifications(notifications)
//                        },
//                        startTransitionState = startTransitionState,
//                        updateTransitionState = { state ->
//                            startTransitionState = state
//                        },
//                        onNotificationClicked = { intent ->
//                            currentUserState?.let { user ->
//                                when (user.authenticationType) {
//                                    AuthenticationType.GESTURE -> {
//                                        onComposeViewListener.remove()
//                                        onComposeViewListener.startIntentApplication(pendingIntent = intent)
//                                    }
//                                    AuthenticationType.PASSWORD -> {
//                                        composeScreenState.value = ComposeScreenState.PassWordScreen
//                                        currentPendingIntent = intent
//                                    }
//                                }
//                            }
//                        }
//                    )
//                }
//
//                AnimatedVisibility(
//                    visible = screenState == ComposeScreenState.PassWordScreen,
//                    enter = fadeIn(),
//                    exit = fadeOut()
//                ) {
//                    PassWordRoute(
//                        unLockPassWordScreen = {
//                            onComposeViewListener.remove()
//                            composeScreenState.value = ComposeScreenState.LockScreen
//                            currentPendingIntent?.let { intent ->
//                                onComposeViewListener.startIntentApplication(intent)
//                                currentPendingIntent = null
//                            }
//                        },
//                        returnLockScreen = {
//                            composeScreenState.value = ComposeScreenState.LockScreen
//                            currentPendingIntent?.let {
//                                currentPendingIntent = null
//                            }
//                        }
//                    )
//                }
            }
        }

        initViewTreeOwner()

        windowManager.addView(composeView, getWindowManagerLayoutParams())
    }

    private fun initViewTreeOwner() {
        lifecycleOwner.apply {
            performRestore(null)
            handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        }
        ViewTreeLifecycleOwner.set(composeView, lifecycleOwner)
        composeView.setViewTreeSavedStateRegistryOwner(lifecycleOwner)

        ViewTreeViewModelStoreOwner.set(composeView) { ViewModelStore() }
        val coroutineContext = AndroidUiDispatcher.CurrentThread
        val runRecomposeScope = CoroutineScope(coroutineContext)
        val recomposer = Recomposer(coroutineContext)
        composeView.compositionContext = recomposer
        runRecomposeScope.launch {
            recomposer.runRecomposeAndApplyChanges()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        serviceUnBind()
    }

    private fun serviceBind() {
        val intent = Intent(this, TestListener::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun serviceUnBind() {
        if (isConnectedService) {
            println("로그 unBind")
            unbindService(serviceConnection)
            isConnectedService = false
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

    fun checkNotificationPermission(): Boolean {
        // Todo : Notification 권한 체크 로직 추가예정
        val sets: Set<String> = NotificationManagerCompat.getEnabledListenerPackages(this)
        if (!sets.contains(packageName)) {
            val intent = Intent(
                Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
            )
            startActivity(intent)
        }
        return sets.contains(packageName)
    }
}
