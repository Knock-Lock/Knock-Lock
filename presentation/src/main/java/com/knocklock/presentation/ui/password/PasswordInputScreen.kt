package com.knocklock.presentation.ui.password

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.knocklock.presentation.ui.theme.KnockLockTheme

@Composable
fun PasswordInputScreen(
    inputtedPassword: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        PasswordInputFieldLayout(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp),
            password = inputtedPassword
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
                        .padding(horizontal = 24.dp, vertical = 40.dp)
                )
            }
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
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}