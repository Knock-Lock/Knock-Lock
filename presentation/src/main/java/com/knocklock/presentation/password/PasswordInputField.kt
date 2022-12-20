package com.knocklock.presentation.password

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knocklock.presentation.ui.theme.KnockLockTheme

@Composable
fun PasswordInputField(
    modifier: Modifier = Modifier,
    number: String
) {
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp),
        horizontalAlignment = CenterHorizontally
    ) {
        Text(
            text = number,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp)
                .height(1.dp),
            color = LocalContentColor.current
        )
    }
}

@Composable
fun PasswordInputFieldLayout(
    modifier: Modifier = Modifier,
    password: String,
    maxPasswordLength: Int = 5
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        for (index in 0..maxPasswordLength) {
            val number = password.getOrNull(index)?.toString() ?: ""
            key(index, number) {
                PasswordInputField(
                    number = number,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview("DarkMode", uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
fun PasswordInputFieldPrev() {
    KnockLockTheme {
        Surface(color = MaterialTheme.colorScheme.primary) {
            PasswordInputField(
                number = "0"
            )
        }
    }
}

@Preview("DarkMode", uiMode = UI_MODE_NIGHT_YES)
@Preview
@Composable
fun PasswordInputFieldLayoutPrev() {
    KnockLockTheme {
        Surface(color = MaterialTheme.colorScheme.primary) {
            PasswordInputFieldLayout(
                password = "123"
            )
        }
    }
}