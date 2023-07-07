package com.knocklock.presentation.setting.menu

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.knocklock.presentation.R
import com.knocklock.presentation.setting.UserSettings

@Composable
fun NormalMenu(
    onMenuSelect: (Int) -> Unit,
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .clickable { onMenuSelect(titleRes) }
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = modifier.padding(start = 16.dp),
            text = stringResource(titleRes),
            color = Color.DarkGray
        )
    }
}

@Composable
fun SwitchMenu(
    @StringRes titleRes: Int,
    isChecked: Boolean,
    onSwitchChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            modifier = modifier.padding(start = 16.dp),
            text = stringResource(titleRes),
            color = Color.DarkGray
        )
        Switch(
            modifier = modifier.padding(end = 16.dp),
            checked = isChecked,
            onCheckedChange = onSwitchChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                uncheckedThumbColor = Color.White,
                checkedTrackColor = Color(0xff1fab89),
                uncheckedTrackColor = Color.LightGray,
                checkedBorderColor = Color(0xff1fab89),
                uncheckedBorderColor = Color.LightGray
            )
        )
    }
}

@Composable
fun MenuList(
    onMenuSelect: (Int) -> Unit,
    onPasswordActivatedChange: (Boolean) -> Unit,
    onLockActivatedChange: (Boolean) -> Unit,
    userSettings: UserSettings,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SwitchMenu(
            onSwitchChange = onPasswordActivatedChange,
            isChecked = userSettings.isPasswordActivated,
            titleRes = R.string.activate_password
        )
        Divider(modifier, 1.dp, Color.Transparent)
        NormalMenu(
            onMenuSelect = onMenuSelect,
            titleRes = R.string.change_password
        )
        Divider(modifier, 1.dp, Color.Transparent)
        NormalMenu(
            onMenuSelect = onMenuSelect,
            titleRes = R.string.credit
        )
        Divider(modifier, 30.dp, Color.Transparent)
        SwitchMenu(
            titleRes = R.string.activate_knocklock,
            isChecked = userSettings.isLockActivated,
            onSwitchChange = onLockActivatedChange
        )
    }
}