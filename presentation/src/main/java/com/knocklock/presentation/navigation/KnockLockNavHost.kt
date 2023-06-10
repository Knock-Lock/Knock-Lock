package com.knocklock.presentation.navigation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.knocklock.presentation.MainActivity.Companion.GITHUB_LINK
import com.knocklock.presentation.MainActivity.Companion.KNOCK_LOCK_ACCOUNT
import com.knocklock.presentation.MainActivity.Companion.KNOCK_LOCK_EMAIL_ADDRESS
import com.knocklock.presentation.R
import com.knocklock.presentation.home.HomeRoute
import com.knocklock.presentation.setting.SettingRoute
import com.knocklock.presentation.setting.credit.CreditRoute
import com.knocklock.presentation.setting.credit.TextMenu
import com.knocklock.presentation.setting.password.PasswordSettingRoute
import com.knocklock.presentation.util.showShortToastMessage

@Composable
fun KnockLockNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavigationRoute.HomeGraph.route
    ) {
        homeGraph(
            modifier, navController
        )

        settingGraph(
            modifier, navController
        )
    }
}

fun NavGraphBuilder.homeGraph(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    navigation(
        startDestination = NavigationRoute.HomeGraph.Home.route,
        route = NavigationRoute.HomeGraph.route
    ) {

        composable(route = NavigationRoute.HomeGraph.Home.route) {
            HomeRoute(
                modifier = modifier,
                onClickSetting = {
                    navController.navigate(NavigationRoute.SettingGraph.route)
                },
                viewModel = hiltViewModel()
            )
        }
    }
}


fun NavGraphBuilder.settingGraph(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    navigation(
        startDestination = NavigationRoute.SettingGraph.Setting.route,
        route = NavigationRoute.SettingGraph.route
    ) {
        composable(route = NavigationRoute.SettingGraph.Setting.route) {
            SettingRoute(
                modifier = modifier,
                navigateToPasswordInputScreen = { navController.navigate(NavigationRoute.SettingGraph.Password.route) },
                onMenuSelected = { titleRes ->
                    when (titleRes) {
                        R.string.change_password -> {
                            navController.navigate(NavigationRoute.SettingGraph.Password.route)
                        }
                        R.string.credit -> {
                            navController.navigate(NavigationRoute.SettingGraph.Credit.route)
                        }
                    }
                },
            )
        }
        composable(route = NavigationRoute.SettingGraph.Password.route) {
            PasswordSettingRoute(
                modifier = modifier,
                onSuccessChangePassword = { navController.popBackStack() },
                onClickBackButton = { navController.popBackStack() }
            )
        }
        composable(route = NavigationRoute.SettingGraph.Credit.route) {
            val context = LocalContext.current
            CreditRoute(
                modifier = modifier,
                onIconClick = { navController.popBackStack() },
                onTextClicked = { menu ->
                    when (menu) {
                        TextMenu.GITHUB -> {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(GITHUB_LINK)
                            }
                            context.startActivity(intent)
                        }
                        TextMenu.INQUIRY -> {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:")
                                putExtra(Intent.EXTRA_EMAIL, arrayOf(KNOCK_LOCK_EMAIL_ADDRESS))
                            }
                            context.startActivity(Intent.createChooser(intent, null))
                        }
                        TextMenu.OPENSOURCE -> {
                            // TODO 임시로 깃헙 연결
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(GITHUB_LINK)
                            }
                            context.startActivity(intent)
                        }
                        TextMenu.SERVICE -> {
                            // TODO 임시로 깃헙 연결
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(GITHUB_LINK)
                            }
                            context.startActivity(intent)
                        }
                        TextMenu.DONATE -> {
                            val clipboard =
                                context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("donate", KNOCK_LOCK_ACCOUNT)
                            runCatching {
                                clipboard.setPrimaryClip(clip)
                            }.onSuccess {
                                context.showShortToastMessage(context.getString(R.string.copy_success_msg))
                            }.onFailure {
                                context.showShortToastMessage(context.getString(R.string.copy_failed_msg))
                            }
                        }
                    }
                }
            )
        }
    }
}