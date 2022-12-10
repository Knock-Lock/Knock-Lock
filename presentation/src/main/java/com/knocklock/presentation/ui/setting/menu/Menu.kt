package com.knocklock.presentation.ui.setting.menu

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.knocklock.presentation.R
import com.knocklock.presentation.ui.setting.SettingMenu


@Composable
fun MenuItem(
    modifier: Modifier = Modifier,
    onMenuSelected: () -> Unit,
    item: SettingMenu,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .clickable(
                onClick = onMenuSelected
            )
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        when (item) {
            is SettingMenu.NormalMenu -> {
                Text(
                    modifier = modifier.padding(start = 16.dp),
                    text = item.title,
                    color = Color.DarkGray
                )
            }
            is SettingMenu.SwitchMenu -> {
                Text(
                    modifier = modifier.padding(start = 16.dp),
                    text = item.title,
                    color = Color.DarkGray
                )
                Switch(
                    modifier = modifier.padding(end = 16.dp),
                    checked = item.checked, onCheckedChange = item.onSwitchChanged,
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
    }
}

@Composable
fun MenuList(
    modifier: Modifier = Modifier,
    onMenuSelected: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    checked: Boolean
) {
    Column(modifier = modifier) {
        MenuItem(
            onMenuSelected = onMenuSelected,
            item = SettingMenu.SwitchMenu(
                title = stringResource(R.string.activate_password),
                isChecked = checked,
                onSwitchChanged = onCheckedChange,
                route = stringResource(R.string.activate_password_route)
            )
        )
        Divider(modifier, 1.dp, Color.Transparent)
        MenuItem(
            onMenuSelected = onMenuSelected,
            item = SettingMenu.NormalMenu(
                title = stringResource(R.string.chagne_password),
                route = stringResource(R.string.change_password_route)
            )
        )
        Divider(modifier, 1.dp, Color.Transparent)
        MenuItem(
            onMenuSelected = onMenuSelected,
            item = SettingMenu.NormalMenu(
                title = stringResource(R.string.credit),
                route = stringResource(R.string.credit_route)
            )
        )
    }

}

@Preview
@Composable
private fun PreviewMenuList() {
    MenuItem(
        onMenuSelected = { },
        item = SettingMenu.SwitchMenu(
            title = stringResource(R.string.activate_password),
            isChecked = false,
            onSwitchChanged = { },
            route = stringResource(R.string.activate_password_route)
        )
    )
}