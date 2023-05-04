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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.renderscript.Toolkit
import com.knocklock.domain.model.AuthenticationType
import com.knocklock.presentation.R
import com.knocklock.presentation.lockscreen.model.LockScreen
import com.knocklock.presentation.lockscreen.model.LockScreenBackground
import com.knocklock.presentation.lockscreen.password.PassWordRoute
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.collections.immutable.toImmutableMap

/**
 * @Created by 김현국 2023/03/24
 */

@Composable
fun LockScreenHost(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit,
    vm: LockScreenViewModel = hiltViewModel(),
    onRemoveNotifications: (Array<String>) -> Unit,
) {
    val packageManager = LocalContext.current.packageManager
    LaunchedEffect(key1 = Unit) {
        vm.getGroupNotifications(packageManager)
    }

    val backgroundState by vm.currentBackground.collectAsStateWithLifecycle()
    val composeScreenState by vm.composeScreenState.collectAsStateWithLifecycle()
    val notificationUiState by vm.notificationList.collectAsStateWithLifecycle()
    val currentUserState by vm.currentLockState.collectAsStateWithLifecycle()
    val notificationUiFlagState by vm.notificationUiFlagState.collectAsStateWithLifecycle()

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
                modifier = Modifier,
                notificationUiState = notificationUiState,
                notificationUiFlagState = notificationUiFlagState.toImmutableMap(),
                userSwipe = {
                    currentUserState?.let { user ->
                        when (user.authenticationType) {
                            AuthenticationType.GESTURE -> {
                                onFinish()
                            }

                            AuthenticationType.PASSWORD -> {
                                vm.setComposeScreenState(ComposeScreenState.PassWordScreen)
                            }
                        }
                    }
                },
                onRemoveNotification = { keys -> onRemoveNotifications(keys.toTypedArray()) },
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
                updateNotificationExpandableFlag = vm::updateExpandable
            )
        }

        AnimatedVisibility(
            visible = composeScreenState == ComposeScreenState.PassWordScreen,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            PassWordRoute(
                unLockPassWordScreen = {
                    vm.setComposeScreenState(ComposeScreenState.LockScreen)
                    onFinish()
                },
                returnLockScreen = {
                    println("here in ")
                    vm.setComposeScreenState(ComposeScreenState.LockScreen)
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
