package com.knocklock.presentation.setting

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.knocklock.presentation.R
import com.knocklock.presentation.setting.menu.MenuList

@Composable
fun SettingRoute(
    onMenuSelect: (Int) -> Unit,
    navigateToPasswordInputScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val userSettings by viewModel.userSetting.collectAsState(UserSettings())

    SettingScreen(
        modifier = modifier,
        onMenuSelected = onMenuSelect,
        onPasswordActivatedChange = { isChecked ->
            if (userSettings.password.isNotEmpty()) {
                viewModel.onPasswordActivatedChanged(isChecked)
            } else {
                navigateToPasswordInputScreen()
            }
        },
        onLockActivatedChange = viewModel::onLockActivatedChanged,
        userSettings = userSettings
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    onMenuSelected: (Int) -> Unit,
    onPasswordActivatedChange: (Boolean) -> Unit,
    onLockActivatedChange: (Boolean) -> Unit,
    userSettings: UserSettings,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = { SettingHeader(Modifier.fillMaxWidth()) },
    ) {
        Column(modifier.padding(it)) {
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            )
            SettingBody(
                onMenuSelected,
                onPasswordActivatedChange,
                onLockActivatedChange,
                userSettings,
                Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun SettingBody(
    onMenuSelect: (Int) -> Unit,
    onPasswordActivatedChange: (Boolean) -> Unit,
    onLockActivatedChange: (Boolean) -> Unit,
    userSettings: UserSettings,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier.fillMaxSize(),
        color = Color(0xffEFEEF3)
    ) {
        MenuList(
            onMenuSelect,
            onPasswordActivatedChange,
            onLockActivatedChange,
            userSettings,
            modifier,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingHeader(
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.setting)
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White
        )
    )
}
