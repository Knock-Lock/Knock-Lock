package com.knocklock.presentation.setting.password

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knocklock.presentation.R
import com.knocklock.presentation.extenstions.wiggle
import com.knocklock.presentation.ui.theme.KnockLockTheme
import com.knocklock.presentation.ui.theme.knockLockFontFamily
import com.knocklock.presentation.ui.theme.labelPrimary

@Composable
fun PasswordSettingScreen(
    state: PasswordInputState,
    onTextButtonClick: (String) -> Unit,
    onActionClick: (KeyboardAction) -> Unit,
    onBackButtonClick: () -> Unit,
    onWiggleAnimationEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackButtonClick)

    PasswordSettingContent(
        modifier = modifier,
        state = state,
        onWiggleAnimationEnd = onWiggleAnimationEnd,
        onTextButtonClick = onTextButtonClick,
        onActionClick = onActionClick
    )
}

@Composable
fun PasswordSettingContent(
    state: PasswordInputState,
    onWiggleAnimationEnd: () -> Unit,
    onTextButtonClick: (String) -> Unit,
    onActionClick: (KeyboardAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        PasswordSettingTitle(
            modifier = Modifier.fillMaxWidth(),
            state = state
        )
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(state.contentRes),
                style = TextStyle(
                    fontSize = 17.sp,
                    lineHeight = 22.sp,
                    fontFamily = knockLockFontFamily,
                    fontWeight = FontWeight(400),
                    color = labelPrimary
                )
            )
            PasswordSettingFieldLayout(
                modifier = Modifier
                    .wiggle(
                        isWiggle = state.getWigglePassword(),
                        onWiggleAnimationEnded = onWiggleAnimationEnd
                    )
                    .padding(top = 43.dp),
                password = state.inputPassword,
            )
        }

        NumberKeyboard(
            modifier = Modifier.fillMaxWidth(),
            onTextButtonClick = onTextButtonClick,
            onActionClick = onActionClick
        )
    }
}

@Composable
fun PasswordSettingTitle(
    state: PasswordInputState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(vertical = 27.dp, horizontal = 16.dp),
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = stringResource(state.titleRes),
            style = TextStyle(
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontFamily = knockLockFontFamily,
                fontWeight = FontWeight(600),
                color = labelPrimary
            )
        )

        Text(
            modifier = Modifier.align(Alignment.CenterEnd),
            text = "취소",
            style = TextStyle(
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontFamily = knockLockFontFamily,
                fontWeight = FontWeight(400),
                color = Color(0xFF007BFE),
            )
        )
    }
}


@Preview("비밀번호 입력 화면")
@Composable
private fun PasswordSettingScreenPrev() {
    KnockLockTheme {
        PasswordSettingScreen(
            modifier = Modifier.fillMaxSize(),
            state = PasswordInputState.PasswordNoneState(
                inputPassword = "",
                titleRes = R.string.title_password_setting,
                contentRes = R.string.content_password_setting
            ),
            onTextButtonClick = {},
            onActionClick = {},
            onBackButtonClick = {},
            onWiggleAnimationEnd = {}
        )
    }
}

@Preview("비밀번호 확인 화면")
@Composable
private fun PasswordSettingConfirmScreenPrev() {
    KnockLockTheme {
        PasswordSettingScreen(
            modifier = Modifier.fillMaxSize(),
            state = PasswordInputState.PasswordConfirmState(
                inputPassword = "",
                savedPassword = "",
                isWigglePassword = false,
                titleRes = R.string.title_password_setting,
                contentRes = R.string.content_password_setting
            ),
            onTextButtonClick = {},
            onActionClick = {},
            onBackButtonClick = {},
            onWiggleAnimationEnd = {}
        )
    }
}

@Preview("비밀번호 확인 화면 - 실패")
@Composable
private fun PasswordSettingConfirmFailedScreenPrev() {
    KnockLockTheme {
        PasswordSettingScreen(
            modifier = Modifier.fillMaxSize(),
            state = PasswordInputState.PasswordConfirmState(
                inputPassword = "",
                savedPassword = "",
                isWigglePassword = true,
                titleRes = R.string.title_password_setting,
                contentRes = R.string.content_password_setting
            ),
            onTextButtonClick = {},
            onActionClick = {},
            onBackButtonClick = {},
            onWiggleAnimationEnd = {}
        )
    }
}