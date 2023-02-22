package com.knocklock.presentation.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.knocklock.presentation.R
import com.knocklock.presentation.home.HomeRoute
import com.knocklock.presentation.home.HomeViewModel
import com.knocklock.presentation.home.menu.HomeMenu
import com.knocklock.presentation.setting.SettingRoute
import com.knocklock.presentation.setting.credit.CreditRoute
import com.knocklock.presentation.setting.password.PasswordInputRoute

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
            val vm: HomeViewModel = hiltViewModel()
            val galleryLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
                    it?.let {
                        vm.saveTmpWallPaper(it.toString())
                    }
                }
            HomeRoute(
                modifier = modifier,
                onClickHomeMenu = { homeMenu ->
                    when (homeMenu) {
                        HomeMenu.SETTING -> {
                            navController.navigate(NavigationRoute.SettingGraph.route)
                        }
                        HomeMenu.TMP -> {
                            galleryLauncher.launch("image/*")
                        }
                        HomeMenu.SAVE -> {
                            vm.saveWallPaper()
                        }
                        HomeMenu.CLEAR -> {
                        }
                    }
                },
                viewModel = vm
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