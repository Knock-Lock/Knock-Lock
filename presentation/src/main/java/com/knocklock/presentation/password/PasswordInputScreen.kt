package com.knocklock.presentation.password

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.knocklock.presentation.R
import com.knocklock.presentation.ui.theme.KnockLockTheme

@Composable
fun PasswordInputScreen(
    state: PasswordInputState,
    onClickTextButton: (String) -> Unit,
    onClickAction: (KeyboardAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        when (state) {
            is PasswordInputState.PasswordNoneState -> {
                PasswordInputContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp)
                )
            }
            is PasswordInputState.PasswordConfirmState -> {
                PasswordInputConfirmContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp),
                    state = state
                )
            }
        }

        PasswordInputFieldLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
            password = state.inputPassword
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    MaterialTheme.shapes.extraLarge.copy(
                        bottomEnd = CornerSize(0.dp), bottomStart = CornerSize(0.dp)
                    )
                ),
            color = Color.White
        ) {
            CompositionLocalProvider(LocalContentColor.provides(Color.Black)) {
                NumberKeyboard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 40.dp),
                    onClickTextButton = onClickTextButton,
                    onClickAction = onClickAction
                )
            }
        }
    }
}

@Composable
fun PasswordInputContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.height(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = stringResource(R.string.desc_password_input)
        )
    }
}

@Composable
fun PasswordInputConfirmContent(
    modifier: Modifier = Modifier,
    state: PasswordInputState.PasswordConfirmState
) {
    Column(
        modifier = modifier.height(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = stringResource(R.string.desc_password_input_confirm)
        )
        if (state.mismatchPassword) {
            Text(
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 16.dp),
                color = MaterialTheme.colorScheme.onError,
                text = stringResource(R.string.desc_forget_password)
            )
        }
    }
}


@Preview("비밀번호 입력 화면")
@Composable
fun PasswordInputScreenPrev() {
    KnockLockTheme {
        Surface(color = MaterialTheme.colorScheme.primary) {
            PasswordInputScreen(
                state = PasswordInputState.PasswordNoneState(""),
                onClickTextButton = {},
                onClickAction = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview("비밀번호 확인 화면")
@Composable
fun PasswordInputConfirmScreenPrev() {
    KnockLockTheme {
        Surface(color = MaterialTheme.colorScheme.primary) {
            PasswordInputScreen(
                state = PasswordInputState.PasswordConfirmState(
                    inputPassword = "",
                    savedPassword = "",
                    mismatchPassword = false
                ),
                onClickTextButton = {},
                onClickAction = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview("비밀번호 확인 화면 - 실패")
@Composable
fun PasswordInputConfirmFailedScreenPrev() {
    KnockLockTheme {
        Surface(color = MaterialTheme.colorScheme.primary) {
            PasswordInputScreen(
                state = PasswordInputState.PasswordConfirmState(
                    inputPassword = "",
                    savedPassword = "",
                    mismatchPassword = true
                ),
                onClickTextButton = {},
                onClickAction = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}