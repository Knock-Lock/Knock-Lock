package com.knocklock.presentation.lockscreen.navigation

import android.service.notification.StatusBarNotification
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.get
import androidx.navigation.navigation
import com.knocklock.domain.model.AuthenticationType
import com.knocklock.presentation.lockscreen.LockScreenRoute
import com.knocklock.presentation.lockscreen.password.PassWordRoute
import com.knocklock.presentation.lockscreen.rememberLockScreenStateHolder
import kotlinx.collections.immutable.ImmutableList

/**
 * @Created by 김현국 2023/02/16
 */

@Composable
fun LockScreenNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    notificationList: ImmutableList<StatusBarNotification>,
    onComposeViewListener: OnComposeViewListener
) {
    NavHost(
        navController = navController,
        startDestination = LockScreenNavigationRoute.LockScreenGraph.route
    ) {
        lockScreenGraph(
            modifier,
            navController,
            notificationList,
            onComposeViewListener
        )
    }
}

fun NavGraphBuilder.lockScreenGraph(
    modifier: Modifier = Modifier,
    navController: NavController,
    notificationList: ImmutableList<StatusBarNotification>,
    onComposeViewListener: OnComposeViewListener
) {
    println("로그2-1 :" + notificationList.joinToString("") { it.toString() })
    navigation(
        startDestination = LockScreenNavigationRoute.LockScreenGraph.LockScreen.route,
        route = LockScreenNavigationRoute.LockScreenGraph.route
    ) {
        composable(
            route = LockScreenNavigationRoute.LockScreenGraph.LockScreen.route
        ) {
            val stateHolder = rememberLockScreenStateHolder(context = LocalContext.current)
            val currentLockState by stateHolder.currentLockState.collectAsState()

            val updatedNotificationList by rememberUpdatedState(newValue = notificationList)
            LaunchedEffect(key1 = updatedNotificationList) {
                stateHolder.updateNotificationList(updatedNotificationList)
            }
            val notificationUiState by stateHolder.notificationList.collectAsState()

            LockScreenRoute(
                modifier = modifier,
                notificationUiState,
                userSwipe = {
                    currentLockState?.let { user ->
                        when (user.authenticationType) {
                            AuthenticationType.GESTURE -> {
                                onComposeViewListener.remove()
                            }
                            AuthenticationType.PASSWORD -> {
                                navController.navigate(LockScreenNavigationRoute.LockScreenGraph.PassWordScreen.route)
                            }
                        }
                    }
                },
                onRemoveNotification = { notifications ->
                    onComposeViewListener.removeNotifications(notifications)
                }
            )
        }

        composable(
            route = LockScreenNavigationRoute.LockScreenGraph.PassWordScreen.route
        ) {
            PassWordRoute(
                unLockPassWordScreen = {
                    onComposeViewListener.remove()
                },
                returnLockScreen = {
                    navController.popBackStack()
                }
            )
        }
    }
}

interface OnComposeViewListener {
    fun remove()
    fun removeNotifications(keys: List<String>)
}
