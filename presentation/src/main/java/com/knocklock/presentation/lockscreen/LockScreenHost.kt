package com.knocklock.presentation.lockscreen

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
    onFinish: () -> Unit,
    onRemoveNotifications: (RemovedGroupNotification) -> Unit,
    modifier: Modifier = Modifier,
    lockScreenViewModel: LockScreenViewModel,

) {
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
        targetValue = if (composeScreenState == ComposeScreenState.LockScreen) { 1 } else { 15 },
        label = "",
    )

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        GlideWithBlurLockScreen(
            modifier = Modifier.fillMaxSize(),
            animateRadiusState,
            backgroundState,
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
                                onFinish()
                            }

                            AuthenticationType.PASSWORD -> {
                                lockScreenViewModel.setComposeScreenState(ComposeScreenState.PassWordScreen)
                            }
                        }
                    }
                },
                onRemoveNotification = onRemoveNotifications,
                onNotificationClicked = { intent ->
                    currentUserState?.let { user ->
                        when (user.authenticationType) {
                            AuthenticationType.GESTURE -> {
                            }

                            AuthenticationType.PASSWORD -> {
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
                unLockPassWordScreen = {
                    onFinish()
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
    modifier: Modifier = Modifier,
    radius: Int,
    screen: LockScreen,
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
