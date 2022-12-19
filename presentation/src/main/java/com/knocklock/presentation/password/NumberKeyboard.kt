package com.knocklock.presentation.password

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntSizeAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.knocklock.presentation.R

enum class KeyBoardTextButtonState(val fontScale: Float) {
    PRESSED(1.5f), NONE(1f)
}

enum class KeyBoardImageButtonState(val imageSize: IntSize) {
    PRESSED(IntSize(width = 24, height = 24)),
    NONE(IntSize(width = 16, height = 16))
}


@Composable
fun KeyboardTextButton(
    text: KeyboardButtonType.Text,
    onClickTextButton: () -> Unit,
    modifier: Modifier = Modifier
) {
    var buttonState by remember { mutableStateOf(KeyBoardTextButtonState.NONE) }
    var buttonEnabled by remember { mutableStateOf(true) }
    val animateFontSize by animateFloatAsState(
        targetValue = buttonState.fontScale,
        animationSpec = tween(durationMillis = 200),
        finishedListener = {
            buttonEnabled = true
            buttonState = KeyBoardTextButtonState.NONE
        }
    )

    Box(
        modifier = modifier
            .size(buttonWidth, buttonHeight)
            .clickable(
                enabled = buttonEnabled,
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = {
                    buttonEnabled = false
                    buttonState = KeyBoardTextButtonState.PRESSED
                    onClickTextButton()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.scale(animateFontSize),
            textAlign = TextAlign.Center,
            text = text.text
        )
    }
}


@Composable
fun KeyboardImageButton(
    image: KeyboardButtonType.Image,
    onClickImageButton: () -> Unit,
    modifier: Modifier = Modifier
) {
    var buttonState by remember { mutableStateOf(KeyBoardImageButtonState.NONE) }
    var buttonEnabled by remember { mutableStateOf(true) }

    val animateImageSize by animateIntSizeAsState(
        targetValue = buttonState.imageSize,
        animationSpec = tween(durationMillis = 200),
        finishedListener = {
            buttonEnabled = true
            buttonState = KeyBoardImageButtonState.NONE
        }
    )

    Box(
        modifier = modifier
            .size(width = buttonWidth, height = buttonHeight)
            .clickable(
                enabled = buttonEnabled,
                interactionSource = MutableInteractionSource(),
                indication = null
            ) {
                buttonEnabled = false
                onClickImageButton()
                buttonState = KeyBoardImageButtonState.PRESSED
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = image.drawableRes),
            modifier = Modifier
                .size(animateImageSize.width.dp, animateImageSize.height.dp),
            contentDescription = null
        )
    }
}

@Composable
fun KeyboardEmptyButton(
    modifier: Modifier = Modifier
) {
    Spacer(modifier = modifier.size(buttonWidth, buttonHeight))
}

@Composable
fun NumberKeyboard(
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
                    KeyboardButtonType.Image(R.drawable.ic_backspace)
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
                        text = type,
                        onClickTextButton = { }
                    )
                }
                is KeyboardButtonType.Image -> {
                    KeyboardImageButton(
                        image = type,
                        onClickImageButton = { }
                    )
                }
                is KeyboardButtonType.Empty -> {
                    KeyboardEmptyButton()
                }
            }
        }
    }
}

@Preview
@Composable
fun KeyboardTextButtonPrev() {
    Surface {
        KeyboardTextButton(
            KeyboardButtonType.Text("1"),
            onClickTextButton = {}
        )
    }
}

@Preview
@Composable
fun KeyboardImageButtonPrev() {
    Surface {
        KeyboardImageButton(
            KeyboardButtonType.Image(R.drawable.ic_backspace),
            onClickImageButton = {}
        )
    }
}

@Preview
@Composable
fun KeyboardPrev() {
    Surface {
        NumberKeyboard()
    }
}

sealed interface KeyboardButtonType {
    data class Image(@DrawableRes val drawableRes: Int) : KeyboardButtonType
    data class Text(val text: String) : KeyboardButtonType
    object Empty : KeyboardButtonType
}

private val buttonWidth = 56.dp
private val buttonHeight = 48.dp