package com.knocklock.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.knocklock.presentation.R
import com.knocklock.presentation.setting.menu.SettingMenuList
import com.knocklock.presentation.ui.theme.knockLockFontFamily
import com.knocklock.presentation.ui.theme.labelPrimary

@Composable
fun SettingRoute(
    onMenuSelect: (Int) -> Unit,
    navigateToPasswordInputScreen: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val userSettings by viewModel.userSetting.collectAsState(UserSettings())

    SettingScreen(
        modifier = Modifier.background(color = Color(0xFFF2F2F7)).then(modifier),
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

@Composable
fun SettingScreen(
    onMenuSelected: (Int) -> Unit,
    onPasswordActivatedChange: (Boolean) -> Unit,
    onLockActivatedChange: (Boolean) -> Unit,
    userSettings: UserSettings,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = stringResource(R.string.setting),
            style = TextStyle(
                fontSize = 28.sp,
                lineHeight = 41.sp,
                fontFamily = knockLockFontFamily,
                fontWeight = FontWeight(600),
                color = labelPrimary,
                letterSpacing = 0.4.sp,
            )
        )
        Spacer(modifier = Modifier.height(57.dp))
        SettingMenuList(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            userSettings = userSettings,
            onMenuSelect = onMenuSelected,
            onPasswordActivatedChange = onPasswordActivatedChange,
            onLockActivatedChange = onLockActivatedChange,
        )
    }
}