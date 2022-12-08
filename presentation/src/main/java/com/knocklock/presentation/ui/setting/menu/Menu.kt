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
import com.knocklock.presentation.ui.setting.ActivatePassword
import com.knocklock.presentation.ui.setting.ChangePassword
import com.knocklock.presentation.ui.setting.Credit
import com.knocklock.presentation.ui.setting.Menu


@Composable
fun MenuItem(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onMenuSelected: () -> Unit,
    item: Menu,
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
        val mCheckedState = remember { mutableStateOf(false) }
        Text(
            modifier = modifier.padding(start = 16.dp),
            text = item.title,
            color = Color.DarkGray
        )
        if (item.isNeedSwitch) {
            Switch(
                modifier = modifier.padding(end = 16.dp),
                checked = checked, onCheckedChange = onCheckedChange,
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
    onMenuSelected: () -> Unit,
    onCheckedChange: (Boolean) -> Unit,
    checked: Boolean
) {
    Column(modifier = modifier) {
        MenuItem(
            onMenuSelected = onMenuSelected,
            item = ActivatePassword,
            onCheckedChange = onCheckedChange,
            checked = checked
        )
        Divider(modifier, 1.dp, Color.Transparent)
        MenuItem(
            onMenuSelected = onMenuSelected,
            item = ChangePassword,
            onCheckedChange = onCheckedChange,
            checked = checked
        )
        Divider(modifier, 1.dp, Color.Transparent)
        MenuItem(
            onMenuSelected = onMenuSelected,
            item = Credit,
            onCheckedChange = onCheckedChange,
            checked = checked
        )
    }

}

@Preview
@Composable
private fun PreviewMenuList() {
    MenuItem(
        onMenuSelected = { },
        item = ActivatePassword,
        onCheckedChange = { },
        checked = false
    )
}