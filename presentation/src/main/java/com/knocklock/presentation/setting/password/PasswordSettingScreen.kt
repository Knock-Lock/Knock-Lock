package com.knocklock.presentation.setting.password

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.knocklock.presentation.R
import com.knocklock.presentation.extenstions.wiggle
import com.knocklock.presentation.ui.component.KnockLockTopAppbar
import com.knocklock.presentation.ui.theme.KnockLockTheme

@Composable
fun PasswordSettingScreen(
    state: PasswordInputState,
    onClickTextButton: (String) -> Unit,
    onClickAction: (KeyboardAction) -> Unit,
    onClickBackButton: () -> Unit,
    onWiggleAnimationEnded: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        KnockLockTopAppbar(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(R.string.title_password_setting),
            onClickBackButton = onClickBackButton
        )
        PasswordSettingContent(
            modifier = modifier,
            state = state,
            onClickTextButton = onClickTextButton,
            onClickAction = onClickAction,
            onWiggleAnimationEnded = onWiggleAnimationEnded
        )
    }
}

@Composable
fun PasswordSettingContent(
    state: PasswordInputState,
    onClickTextButton: (String) -> Unit,
    onClickAction: (KeyboardAction) -> Unit,
    onWiggleAnimationEnded: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            PasswordSettingTitle(state = state)
            PasswordSettingFieldLayout(
                modifier = Modifier
                    .wiggle(
                        isWiggle = state.getWigglePassword(),
                        onWiggleAnimationEnded = onWiggleAnimationEnded
                    )
                    .padding(top = 32.dp),
                password = state.inputPassword,
            )
        }
        NumberKeyboard(
            modifier = Modifier
                .padding(bottom = 24.dp)
                .padding(horizontal = 24.dp),
            onClickTextButton = onClickTextButton,
            onClickAction = onClickAction
        )
    }
}

@Composable
fun PasswordSettingTitle(
    state: PasswordInputState,
) {
    when (state) {
        is PasswordInputState.PasswordNoneState -> {
            PasswordSettingNoneTitle(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp)
            )
        }

        is PasswordInputState.PasswordConfirmState -> {
            PasswordSettingConfirmTitle(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp),
                state = state
            )
        }

        is PasswordInputState.PasswordVerifyState -> {
            PasswordSettingVerifyTitle(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp),
                state = state
            )
        }
    }
}

@Composable
fun PasswordSettingNoneTitle(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = stringResource(R.string.desc_password_setting)
        )
    }
}

@Composable
fun PasswordSettingVerifyTitle(
    modifier: Modifier = Modifier,
    state: PasswordInputState.PasswordVerifyState
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = stringResource(R.string.desc_password_verify)
        )
    }
}

@Composable
fun PasswordSettingConfirmTitle(
    modifier: Modifier = Modifier,
    state: PasswordInputState.PasswordConfirmState
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = stringResource(R.string.desc_password_setting_confirm)
        )
        AnimatedVisibility(visible = state.isWigglePassword) {
            Text(
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 16.dp),
                text = stringResource(R.string.desc_forget_password)
            )
        }
    }
}


@Preview("비밀번호 입력 화면")

@Composable
private fun PasswordSettingScreenPrev() {
    KnockLockTheme {
        Surface(color = MaterialTheme.colorScheme.primary) {
            PasswordSettingScreen(
                modifier = Modifier.fillMaxSize(),
                state = PasswordInputState.PasswordNoneState(""),
                onClickTextButton = {},
                onClickAction = {},
                onClickBackButton = {},
                onWiggleAnimationEnded = {}
            )
        }
    }
}

@Preview("비밀번호 확인 화면")

@Composable
private fun PasswordSettingConfirmScreenPrev() {
    KnockLockTheme {
        Surface(color = MaterialTheme.colorScheme.primary) {
            PasswordSettingScreen(
                modifier = Modifier.fillMaxSize(),
                state = PasswordInputState.PasswordConfirmState(
                    inputPassword = "",
                    savedPassword = "",
                    isWigglePassword = false
                ),
                onClickTextButton = {},
                onClickAction = {},
                onClickBackButton = {},
                onWiggleAnimationEnded = {}
            )
        }
    }
}

@Preview("비밀번호 확인 화면 - 실패")

@Composable
private fun PasswordSettingConfirmFailedScreenPrev() {
    KnockLockTheme {
        Surface(color = MaterialTheme.colorScheme.primary) {
            PasswordSettingScreen(
                modifier = Modifier.fillMaxSize(),
                state = PasswordInputState.PasswordConfirmState(
                    inputPassword = "",
                    savedPassword = "",
                    isWigglePassword = true
                ),
                onClickTextButton = {},
                onClickAction = {},
                onClickBackButton = {},
                onWiggleAnimationEnded = {}
            )
        }
    }
}