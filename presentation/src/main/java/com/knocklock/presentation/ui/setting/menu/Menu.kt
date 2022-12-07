package com.knocklock.presentation.ui.setting.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

interface Menu {
    val title: String
    val isNeedSwitch: Boolean
    val route: String
}

object ActivatePassword : Menu {
    override val title = "비밀번호 활성화"
    override val isNeedSwitch = false
    override val route = "activate_password"
}

object ChangePassword : Menu {
    override val title = "비밀번호 변경"
    override val isNeedSwitch = true
    override val route = "change_password"
}

object Credit : Menu {
    override val title = "크레딧"
    override val isNeedSwitch = false
    override val route = "credit"
}

@Composable
fun MenuItem(
    modifier: Modifier = Modifier,
    menuClickEvent: () -> Unit,
    item: Menu,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .clickable(
                onClick = menuClickEvent
            )
            .background(Color.White),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val mCheckedState = remember { mutableStateOf(false) }
        Text(
            modifier = modifier.padding(start = 16.dp),
            text = item.title,
            color = Color.DarkGray
        )
        if (item.isNeedSwitch) {
            Switch(
                modifier = modifier.padding(end = 16.dp),
                checked = mCheckedState.value, onCheckedChange = { mCheckedState.value = it },
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

@Composable
fun MenuList(
    modifier: Modifier = Modifier,
    menuClickEvent: () -> Unit
) {
    Column(modifier = modifier) {
        MenuItem(
            menuClickEvent = menuClickEvent,
            item = ActivatePassword
        )
        Divider(modifier, 1.dp, Color.Transparent)
        MenuItem(
            menuClickEvent = menuClickEvent,
            item = ChangePassword
        )
        Divider(modifier, 1.dp, Color.Transparent)
        MenuItem(
            menuClickEvent = menuClickEvent,
            item = Credit
        )
    }

}

@Preview
@Composable
private fun PreviewMenuList() {
    MenuList(menuClickEvent = {})
}