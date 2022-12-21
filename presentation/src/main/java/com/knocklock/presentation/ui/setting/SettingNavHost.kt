package com.knocklock.presentation.ui.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.knocklock.presentation.R
import com.knocklock.presentation.ui.setting.credit.CreditRoute

@Composable
fun SettingNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Setting.route,
        modifier = modifier
    ) {
        composable(route = Setting.route) {
            SettingRoute(
                modifier = modifier,
                onMenuSelected = { titleRes ->
                    when (titleRes) {
                        R.string.change_password -> {
                            navController.navigate(Password.route)
                        }
                        R.string.credit -> {
                            navController.navigate(Credit.route)
                        }
                    }
                },
            )
        }
        composable(route = Password.route) {
            /* TODO */
        }
        composable(route = Credit.route) {
            CreditRoute(
                modifier = modifier,
                onIconClick = { navController.popBackStack() }
            )
        }
    }
}