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
import com.knocklock.presentation.ui.setting.UserSettings

@Composable
fun NormalMenu(
    modifier: Modifier = Modifier,
    onMenuSelected: () -> Unit,
    title: String
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
            text = title,
            color = Color.DarkGray
        )
    }
}

@Composable
fun SwitchMenu(
    modifier: Modifier = Modifier,
    title: String,
    isChecked: Boolean,
    onSwitchChanged: (Boolean) -> Unit,
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
            text = title,
            color = Color.DarkGray
        )
        Switch(
            modifier = modifier.padding(end = 16.dp),
            checked = isChecked,
            onCheckedChange = onSwitchChanged,
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
    onChangedPasswordActivated: (Boolean) -> Unit,
    userSettings: UserSettings
) {
    Column(modifier = modifier) {
        SwitchMenu(
            onSwitchChanged = onChangedPasswordActivated,
            isChecked = userSettings.isPasswordActivated,
            title = stringResource(R.string.activate_password)
        )
        Divider(modifier, 1.dp, Color.Transparent)
        NormalMenu(
            onMenuSelected = onMenuSelected,
            title = stringResource(R.string.chagne_password)
        )
        Divider(modifier, 1.dp, Color.Transparent)
        NormalMenu(
            onMenuSelected = onMenuSelected,
            title = stringResource(R.string.credit)
        )
    }
}

@Preview
@Composable
private fun PreviewMenuList() {
    SwitchMenu(
        onSwitchChanged = { },
        isChecked = false,
        title = "test"
    )
}