package com.knocklock.presentation.navigation

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
            val context = LocalContext.current
            val galleryLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        result.data?.data?.let { uri ->
                            context.contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            vm.saveTmpWallPaper(uri.toString())
                        }
                    }
                }
            val launcherIntent = Intent(
                Intent.ACTION_GET_CONTENT,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ).apply {
                type = "image/*"
                action = Intent.ACTION_OPEN_DOCUMENT
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)

            }
            HomeRoute(
                modifier = modifier,
                onClickHomeMenu = { homeMenu ->
                    when (homeMenu) {
                        HomeMenu.SETTING -> {
                            navController.navigate(NavigationRoute.SettingGraph.route)
                        }
                        HomeMenu.TMP -> {
                            galleryLauncher.launch(launcherIntent)
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