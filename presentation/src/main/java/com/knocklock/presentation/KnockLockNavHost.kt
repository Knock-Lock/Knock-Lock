package com.knocklock.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.knocklock.presentation.ui.setting.SettingRoute
import com.knocklock.presentation.ui.setting.credit.CreditRoute

@Composable
fun KnockLockNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = NavigationRoute.SettingGraph.route
    ) {
        settingGraph(
            modifier, navController
        )
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
            /* TODO */
        }
        composable(route = NavigationRoute.SettingGraph.Credit.route) {
            CreditRoute(
                modifier = modifier,
                onIconClick = { navController.popBackStack() }
            )
        }
    }
}