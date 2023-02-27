package com.knocklock.presentation.lockscreen.service

import android.app.PendingIntent
import android.content.pm.ActivityInfo
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.service.notification.StatusBarNotification
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.compositionContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.knocklock.domain.model.AuthenticationType
import com.knocklock.domain.model.LockScreenBackground
import com.knocklock.presentation.R
import com.knocklock.presentation.lockscreen.LockScreenRoute
import com.knocklock.presentation.lockscreen.password.PassWordRoute
import com.knocklock.presentation.lockscreen.rememberLockScreenStateHolder
import com.knocklock.presentation.lockscreen.util.ComposeLifecycleOwner
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @Created by 김현국 2023/01/05
 * @Time 3:47 PM
 */
class InitLockScreenView(
    private val composeView: ComposeView,
    private val onComposeViewListener: OnComposeViewListener,
    private val point: Point
) {
    private val lifecycleOwner by lazy { ComposeLifecycleOwner() }
    private val composeViewModelStore by lazy { ViewModelStore() }

    private val notificationList = MutableStateFlow(emptyList<StatusBarNotification>())

    private val composeScreenState = MutableStateFlow<ComposeScreenState>(ComposeScreenState.LockScreen)

    private var currentPendingIntent: PendingIntent? = null

    init {
        createComposeLockScreenView()
    }

    private fun createComposeLockScreenView() {
        composeView.setContent {
            val screenState by composeScreenState.collectAsState()
            var startTransitionState by remember { mutableStateOf(false) }
            val stateHolder = rememberLockScreenStateHolder(LocalContext.current)
            val backgroundState by stateHolder.currentBackground.collectAsState()

            val request = ImageRequest.Builder(LocalContext.current)
                .allowHardware(false)
                .data(
                    data = when (backgroundState) {
                        is LockScreenBackground.DefaultWallPaper -> {
                            R.drawable.default_wallpaper
                        }
                        is LockScreenBackground.LocalImage -> {
                            (backgroundState as LockScreenBackground.LocalImage).imageUri
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
                AnimatedVisibility(
                    visible = screenState == ComposeScreenState.LockScreen,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val notificationListState by notificationList.collectAsState()
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
                                        onComposeViewListener.remove()
                                    }
                                    AuthenticationType.PASSWORD -> {
                                        composeScreenState.value = ComposeScreenState.PassWordScreen
                                    }
                                }
                            }
                        },
                        onRemoveNotification = { notifications ->
                            onComposeViewListener.removeNotifications(notifications)
                        },
                        startTransitionState = startTransitionState,
                        updateTransitionState = { state ->
                            startTransitionState = state
                        },
                        onNotificationClicked = { intent ->
                            currentUserState?.let { user ->
                                when (user.authenticationType) {
                                    AuthenticationType.GESTURE -> {
                                        onComposeViewListener.remove()
                                        onComposeViewListener.startIntentApplication(pendingIntent = intent)
                                    }
                                    AuthenticationType.PASSWORD -> {
                                        composeScreenState.value = ComposeScreenState.PassWordScreen
                                        currentPendingIntent = intent
                                    }
                                }
                            }
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
                            onComposeViewListener.remove()
                            composeScreenState.value = ComposeScreenState.LockScreen
                            currentPendingIntent?.let { intent ->
                                onComposeViewListener.startIntentApplication(intent)
                                currentPendingIntent = null
                            }
                        },
                        returnLockScreen = {
                            composeScreenState.value = ComposeScreenState.LockScreen
                            currentPendingIntent?.let {
                                currentPendingIntent = null
                            }
                        }
                    )
                }
            }
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
    fun remove()
    fun removeNotifications(keys: List<String>)
    fun startIntentApplication(pendingIntent: PendingIntent)
}

sealed class ComposeScreenState {
    object LockScreen : ComposeScreenState()
    object PassWordScreen : ComposeScreenState()
}
