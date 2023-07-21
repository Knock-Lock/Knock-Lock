package com.knocklock.presentation.lockscreen

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.renderscript.Toolkit
import com.knocklock.domain.model.AuthenticationType
import com.knocklock.presentation.R
import com.knocklock.presentation.lockscreen.model.LockScreen
import com.knocklock.presentation.lockscreen.model.LockScreenBackground
import com.knocklock.presentation.lockscreen.model.RemovedGroupNotification
import com.knocklock.presentation.lockscreen.password.PassWordRoute
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap

/**
 * @Created by 김현국 2023/03/24
 */

@Composable
fun LockScreenHost(
    onFinished: () -> Unit,
    onNotificationsRemove: (RemovedGroupNotification) -> Unit,
    lockScreenViewModel: LockScreenViewModel,
    modifier: Modifier = Modifier,

) {
    val context = LocalContext.current
    val packageManager = LocalContext.current.packageManager
    LaunchedEffect(key1 = Unit) {
        lockScreenViewModel.getGroupNotifications(packageManager)
    }
    val backgroundState by lockScreenViewModel.currentBackground.collectAsStateWithLifecycle()
    val composeScreenState by lockScreenViewModel.composeScreenState.collectAsStateWithLifecycle()
    val oldNotificationUiState by lockScreenViewModel.oldNotificationList.collectAsStateWithLifecycle()
    val oldNotificationUiFlagState by lockScreenViewModel.oldNotificationUiFlagState.collectAsStateWithLifecycle()
    val recentNotificationList by lockScreenViewModel.recentNotificationList.collectAsStateWithLifecycle()
    val recentNotificationUiFlagState by lockScreenViewModel.recentNotificationUiFlagState.collectAsStateWithLifecycle()
    val currentUserState by lockScreenViewModel.currentLockState.collectAsStateWithLifecycle()
    val animateRadiusState by animateIntAsState(
        targetValue = if (composeScreenState == ComposeScreenState.LockScreen) {
            1
        } else {
            15
        },
        label = "",
    )

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        GlideWithBlurLockScreen(
            modifier = Modifier.fillMaxSize(),
            radius = animateRadiusState,
            screen = backgroundState,
        )

        AnimatedVisibility(
            visible = composeScreenState == ComposeScreenState.LockScreen,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            LockScreenRoute(
                recentNotificationList = recentNotificationList.toImmutableList(),
                recentNotificationUiFlagState = recentNotificationUiFlagState.toImmutableMap(),
                oldNotificationUiState = oldNotificationUiState,
                oldNotificationUiFlagState = oldNotificationUiFlagState.toImmutableMap(),
                userSwipe = {
                    currentUserState?.let { user ->
                        when (user.authenticationType) {
                            AuthenticationType.GESTURE -> {
                                onFinished()
                            }

                            AuthenticationType.PASSWORD -> {
                                lockScreenViewModel.setComposeScreenState(ComposeScreenState.PassWordScreen)
                            }
                        }
                    }
                },
                onNotificationRemove = onNotificationsRemove,
                onNotificationClick = { packageName ->
                    currentUserState?.let { user ->
                        when (user.authenticationType) {
                            AuthenticationType.GESTURE -> {
                                val launchIntent: Intent? = packageManager.getLaunchIntentForPackage(packageName)
                                launchIntent?.let { intent ->
                                    startActivity(context, intent, null)
                                }
                                onFinished()
                            }

                            AuthenticationType.PASSWORD -> {
                                val launchIntent: Intent? = packageManager.getLaunchIntentForPackage(packageName)
                                launchIntent?.let { intent ->
                                    startActivity(context, intent, null)
                                }
                                lockScreenViewModel.setComposeScreenState(ComposeScreenState.PassWordScreen)
                            }
                        }
                    }
                },
                updateOldNotificationExpandableFlag = lockScreenViewModel::updateOldNotificationExpandable,
                updateRecentNotificationExpandableFlag = lockScreenViewModel::updateRecentNotificationExpandable,
                updateNotificationClickableFlag = lockScreenViewModel::updateClickable,
                timeFormat = backgroundState.timeFormat,
            )
        }

        AnimatedVisibility(
            visible = composeScreenState == ComposeScreenState.PassWordScreen,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            PassWordRoute(
                onPassWordScreenUnLock = {
                    onFinished()
                },
                returnLockScreen = {
                    lockScreenViewModel.setComposeScreenState(ComposeScreenState.LockScreen)
                },
            )
        }
    }
}

@Composable
fun GlideWithBlurLockScreen(
    radius: Int,
    screen: LockScreen,
    modifier: Modifier = Modifier,
) {
    GlideImage(
        modifier = modifier,
        imageModel = {
            when (screen.background) {
                is LockScreenBackground.DefaultWallPaper -> {
                    R.drawable.default_wallpaper
                }

                is LockScreenBackground.LocalImage -> {
                    screen.background.imageUri
                }
            }
        },
        success = { imageState ->
            imageState.imageBitmap?.asAndroidBitmap()?.copy(Bitmap.Config.ARGB_8888, true)?.let {
                Toolkit.blur(it, radius)
            }?.let {
                Image(
                    modifier = modifier,
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                )
            }
        },
    )
}
