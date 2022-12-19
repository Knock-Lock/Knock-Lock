package com.knocklock.presentation.password

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
    inputtedPassword: String,
    onClickTextButton: (String) -> Unit,
    onClickAction: (KeyboardAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        PasswordInputContent(
            modifier = Modifier.fillMaxWidth(),
            inputtedPassword = inputtedPassword
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
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
    modifier: Modifier = Modifier,
    inputtedPassword: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(top = 60.dp),
            text = stringResource(id = R.string.desc_password_input)
        )

        PasswordInputFieldLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
            password = inputtedPassword
        )
    }
}

@Preview
@Composable
fun PasswordInputContentPrev() {
    KnockLockTheme {
        Surface(color = MaterialTheme.colorScheme.primary) {
            PasswordInputContent(
                inputtedPassword = "",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Preview
@Composable
fun PasswordInputScreenPrev() {
    KnockLockTheme {
        Surface(color = MaterialTheme.colorScheme.primary) {
            PasswordInputScreen(
                inputtedPassword = "",
                onClickTextButton = {},
                onClickAction = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}