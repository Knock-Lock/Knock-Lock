package com.knocklock.presentation.navigation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.systemBarsPadding
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
import com.knocklock.presentation.MainActivity.Companion.GithubLink
import com.knocklock.presentation.MainActivity.Companion.KknokLockAccount
import com.knocklock.presentation.MainActivity.Companion.KnockLockEmailAddress
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
            navController, modifier,
        )

        settingGraph(
            navController, modifier,
        )
    }
}

fun NavGraphBuilder.homeGraph(
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    navigation(
        startDestination = NavigationRoute.HomeGraph.Home.route,
        route = NavigationRoute.HomeGraph.route
    ) {

        composable(route = NavigationRoute.HomeGraph.Home.route) {
            HomeRoute(
                modifier = modifier,
                onSettingClick = {
                    navController.navigate(NavigationRoute.SettingGraph.route)
                },
                viewModel = hiltViewModel()
            )
        }
    }
}


fun NavGraphBuilder.settingGraph(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    navigation(
        startDestination = NavigationRoute.SettingGraph.Setting.route,
        route = NavigationRoute.SettingGraph.route
    ) {
        composable(route = NavigationRoute.SettingGraph.Setting.route) {
            SettingRoute(
                modifier = modifier.systemBarsPadding(),
                navigateToPasswordInputScreen = { navController.navigate(NavigationRoute.SettingGraph.Password.route) },
                onMenuSelect = { titleRes ->
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
                modifier = modifier.systemBarsPadding(),
                onPasswordChangeSuccess = { navController.popBackStack() },
                onBackButtonClick = { navController.popBackStack() }
            )
        }
        composable(route = NavigationRoute.SettingGraph.Credit.route) {
            val context = LocalContext.current
            CreditRoute(
                modifier = modifier,
                onIconClick = { navController.popBackStack() },
                onTextClick = { menu ->
                    when (menu) {
                        TextMenu.Github -> {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(GithubLink)
                            }
                            context.startActivity(intent)
                        }

                        TextMenu.Inqury -> {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:")
                                putExtra(Intent.EXTRA_EMAIL, arrayOf(KnockLockEmailAddress))
                            }
                            context.startActivity(Intent.createChooser(intent, null))
                        }

                        TextMenu.OpenSource -> {
                            // TODO 임시로 깃헙 연결
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(GithubLink)
                            }
                            context.startActivity(intent)
                        }

                        TextMenu.Service -> {
                            // TODO 임시로 깃헙 연결
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(GithubLink)
                            }
                            context.startActivity(intent)
                        }

                        TextMenu.Donate -> {
                            val clipboard =
                                context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("donate", KknokLockAccount)
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