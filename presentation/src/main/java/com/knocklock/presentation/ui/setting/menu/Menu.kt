package com.knocklock.presentation.ui.setting.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.knocklock.presentation.ui.setting.MenuType
import com.knocklock.presentation.ui.setting.SettingMenu
import kotlinx.collections.immutable.ImmutableList

@Composable
fun NormalMenu(
    modifier: Modifier = Modifier,
    onMenuSelected: () -> Unit,
    menu: SettingMenu.NormalMenu
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
        Text(
            modifier = modifier.padding(start = 16.dp),
            text = stringResource(menu.type.titleRes),
            color = Color.DarkGray
        )
    }
}

@Composable
fun SwitchMenu(
    modifier: Modifier = Modifier,
    onMenuSelected: () -> Unit,
    onSwitchChanged: (Boolean) -> Unit,
    menu: SettingMenu.SwitchMenu
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
        Text(
            modifier = modifier.padding(start = 16.dp),
            text = stringResource(menu.type.titleRes),
            color = Color.DarkGray
        )
        Switch(
            modifier = modifier.padding(end = 16.dp),
            checked = menu.checked, onCheckedChange = onSwitchChanged,
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
    modifier: Modifier = Modifier,
    onMenuSelected: () -> Unit,
    onSwitchChanged: (Boolean) -> Unit,
    checked: Boolean,
    menuList: ImmutableList<SettingMenu>
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        items(menuList) { menu ->
            when (menu) {
                is SettingMenu.SwitchMenu -> {
                    SwitchMenu(
                        onMenuSelected = onMenuSelected,
                        onSwitchChanged = onSwitchChanged,
                        menu = menu.copy(isChecked = checked)
                    )
                }
                is SettingMenu.NormalMenu -> {
                    NormalMenu(
                        onMenuSelected = onMenuSelected,
                        menu = menu
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewMenuList() {
    SwitchMenu(
        onMenuSelected = { },
        onSwitchChanged = { },
        menu = SettingMenu.SwitchMenu(MenuType.ACTIVATE_PASSWORD)
    )
}