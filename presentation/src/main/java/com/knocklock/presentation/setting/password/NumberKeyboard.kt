package com.knocklock.presentation.setting.password

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knocklock.presentation.R
import com.knocklock.presentation.extenstions.noRippleClickable

@Composable
fun KeyboardTextButton(
    textButtonItem: KeyboardButtonType.Text,
    onTextButtonClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .noRippleClickable {
                onTextButtonClick(textButtonItem.text)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
            text = textButtonItem.text,
            fontSize = 32.sp
        )
    }
}


@Composable
fun KeyboardImageButton(
    imageButtonItem: KeyboardButtonType.Image,
    onImageButtonClick: (KeyboardAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .noRippleClickable {
                onImageButtonClick(imageButtonItem.action)
            },
        contentAlignment = Alignment.Center,
    ) {
        Image(
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.Center),
            painter = painterResource(id = imageButtonItem.drawableRes),
            contentDescription = null,
        )
    }
}

@Composable
fun KeyboardEmptyButton(
    modifier: Modifier = Modifier
) {
    Spacer(modifier = modifier)
}

@Composable
fun NumberKeyboard(
    onTextButtonClick: (String) -> Unit,
    onActionClick: (KeyboardAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val buttons = remember {
        mutableListOf<KeyboardButtonType>().apply {
            addAll(
                listOf(
                    KeyboardButtonType.Text("1"),
                    KeyboardButtonType.Text("2"),
                    KeyboardButtonType.Text("3")
                )
            )
            addAll(
                listOf(
                    KeyboardButtonType.Text("4"),
                    KeyboardButtonType.Text("5"),
                    KeyboardButtonType.Text("6")
                )
            )
            addAll(
                listOf(
                    KeyboardButtonType.Text("7"),
                    KeyboardButtonType.Text("8"),
                    KeyboardButtonType.Text("9")
                )
            )
            addAll(
                listOf(
                    KeyboardButtonType.Empty,
                    KeyboardButtonType.Text("0"),
                    KeyboardButtonType.Image(
                        action = KeyboardAction.BackSpace,
                        drawableRes = R.drawable.ic_backspace
                    )
                )
            )
        }.toList()
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = modifier.fillMaxWidth()
    ) {
        items(buttons) { type ->
            when (type) {
                is KeyboardButtonType.Text -> {
                    KeyboardTextButton(
                        modifier = Modifier.aspectRatio(1.25f),
                        textButtonItem = type,
                        onTextButtonClick = onTextButtonClick,
                    )
                }

                is KeyboardButtonType.Image -> {
                    KeyboardImageButton(
                        modifier = Modifier.aspectRatio(1.25f),
                        imageButtonItem = type,
                        onImageButtonClick = onActionClick,
                    )
                }

                is KeyboardButtonType.Empty -> {
                    KeyboardEmptyButton(
                        modifier = Modifier.aspectRatio(1.25f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun KeyboardTextButtonPrev() {
    Surface {
        KeyboardTextButton(
            KeyboardButtonType.Text("1"),
            onTextButtonClick = {}
        )
    }
}

@Preview
@Composable
private fun KeyboardImageButtonPrev() {
    Surface {
        KeyboardImageButton(
            KeyboardButtonType.Image(KeyboardAction.BackSpace, R.drawable.ic_backspace),
            onImageButtonClick = {}
        )
    }
}

@Preview
@Composable
private fun KeyboardPrev() {
    Surface {
        NumberKeyboard(
            onTextButtonClick = {},
            onActionClick = {}
        )
    }
}

sealed interface KeyboardButtonType {
    data class Image(
        val action: KeyboardAction,
        @DrawableRes val drawableRes: Int
    ) : KeyboardButtonType

    data class Text(val text: String) : KeyboardButtonType
    object Empty : KeyboardButtonType
}

enum class KeyboardAction {
    BackSpace
}