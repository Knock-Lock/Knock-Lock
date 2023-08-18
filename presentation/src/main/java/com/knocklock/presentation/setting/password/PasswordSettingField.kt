package com.knocklock.presentation.setting.password

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.knocklock.presentation.ui.theme.KnockLockTheme

@Composable
fun PasswordSettingField(
    number: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .run {
                if (number.isBlank()) {
                    border(
                        width = 1.dp,
                        color = Color.Black,
                        shape = CircleShape
                    )
                } else {
                    background(
                        color = Color.Black,
                        shape = CircleShape
                    )
                }
            }
            .size(20.dp)
    )
}

@Composable
fun PasswordSettingFieldLayout(
    password: String,
    modifier: Modifier = Modifier,
    maxPasswordLength: Int = 5
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        for (index in 0..maxPasswordLength) {
            val number = password.getOrNull(index)?.toString() ?: ""
            key(index, number) {
                PasswordSettingField(
                    number = number
                )
            }
        }
    }
}

@Preview
@Composable
private fun PasswordSettingFieldPrev() {
    KnockLockTheme {
        Surface(color = MaterialTheme.colorScheme.primary) {
            PasswordSettingField(
                number = "0"
            )
        }
    }
}

@Preview
@Composable
private fun PasswordSettingFieldLayoutPrev() {
    KnockLockTheme {
        Surface(color = MaterialTheme.colorScheme.primary) {
            PasswordSettingFieldLayout(
                password = "123"
            )
        }
    }
}