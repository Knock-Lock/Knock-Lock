package com.knocklock.presentation.setting.menu

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knocklock.presentation.R
import com.knocklock.presentation.setting.UserSettings
import com.knocklock.presentation.ui.theme.Green
import com.knocklock.presentation.ui.theme.Grey3
import com.knocklock.presentation.ui.theme.Tertiary
import com.knocklock.presentation.ui.theme.White
import com.knocklock.presentation.ui.theme.knockLockFontFamily
import com.knocklock.presentation.ui.theme.labelPrimary

@Composable
fun NormalMenu(
    onMenuSelect: (Int) -> Unit,
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(menuShape)
            .clickable { if (isActive) onMenuSelect(titleRes) }
            .background(Color.White, menuShape)
            .padding(horizontal = 20.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(titleRes),
            style = TextStyle(
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontFamily = knockLockFontFamily,
                fontWeight = FontWeight(400),
                color = if (isActive) labelPrimary else Color(0xFFDBDBDB),
            )
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
            .clip(menuShape)
            .background(Color.White, menuShape)
            .padding(horizontal = 20.dp, vertical = 6.5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(titleRes),
            style = TextStyle(
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontFamily = knockLockFontFamily,
                fontWeight = FontWeight(400),
                color = labelPrimary,
            )
        )

        Switch(
            modifier = Modifier.size(51.dp, 31.dp),
            checked = isChecked,
            onCheckedChange = onSwitchChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = White,
                uncheckedThumbColor = White,
                checkedTrackColor = Green,
                uncheckedTrackColor = Tertiary,
                checkedBorderColor = Color.Transparent,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

@Composable
fun SettingMenuList(
    onMenuSelect: (Int) -> Unit,
    onPasswordActivatedChange: (Boolean) -> Unit,
    onLockActivatedChange: (Boolean) -> Unit,
    userSettings: UserSettings,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SwitchMenu(
            titleRes = R.string.activate_knocklock,
            isChecked = userSettings.isLockActivated,
            onSwitchChange = onLockActivatedChange
        )
        Spacer(modifier = Modifier.height(35.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White, menuShape)
        ) {
            SwitchMenu(
                onSwitchChange = onPasswordActivatedChange,
                isChecked = userSettings.isPasswordActivated,
                titleRes = R.string.activate_password
            )
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                thickness = 1.dp,
                color = Grey3
            )
            NormalMenu(
                onMenuSelect = onMenuSelect,
                titleRes = R.string.change_password,
                isActive = userSettings.isPasswordActivated
            )
        }
        Spacer(modifier = Modifier.height(35.dp))
        NormalMenu(
            onMenuSelect = onMenuSelect,
            titleRes = R.string.credit
        )
    }
}

private val menuShape = RoundedCornerShape(10.dp)