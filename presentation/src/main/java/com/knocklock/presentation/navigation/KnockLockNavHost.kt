package com.knocklock.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.knocklock.presentation.R
import com.knocklock.presentation.home.HomeRoute
import com.knocklock.presentation.home.HomeScreenUiState
import com.knocklock.presentation.home.menu.HomeMenu
import com.knocklock.presentation.setting.password.PasswordInputRoute
import com.knocklock.presentation.setting.SettingRoute
import com.knocklock.presentation.setting.credit.CreditRoute
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

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
    navController: NavController
) {
    navigation(
        startDestination = NavigationRoute.HomeGraph.Home.route,
        route = NavigationRoute.HomeGraph.route
    ) {
        composable(route = NavigationRoute.HomeGraph.Home.route) {
            HomeRoute(
                modifier = modifier,
                onClickHomeMenu = { homeMenu ->
                    when (homeMenu) {
                        HomeMenu.SETTING -> {
                            navController.navigate(NavigationRoute.SettingGraph.route)
                        }
                        else -> {
                        }
                    }
                }
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
            PasswordInputRoute(
                modifier = modifier,
                onSuccessChangePassword = { navController.popBackStack() }
            )
        }
        composable(route = NavigationRoute.SettingGraph.Credit.route) {
            CreditRoute(
                modifier = modifier,
                onIconClick = { navController.popBackStack() }
            )
        }
    }
}