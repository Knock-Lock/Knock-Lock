@file:OptIn(ExperimentalFoundationApi::class)

package com.knocklock.presentation.ui.password

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateIntSizeAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knocklock.presentation.R

enum class KeyBoardTextButtonState(val fontSize: Int) {
    PRESSED(20), NONE(14)
}

enum class KeyBoardImageButtonState(val imageSize: IntSize) {
    PRESSED(IntSize(width = 24, height = 24)),
    NONE(IntSize(width = 16, height = 16))
}


@Composable
fun KeyboardTextButton(
    textButton: KeyboardButtonType.TextButton,
    onClickTextButton: () -> Unit,
    modifier: Modifier = Modifier
) {
    var buttonState by remember { mutableStateOf(KeyBoardTextButtonState.NONE) }
    var buttonEnabled by remember { mutableStateOf(true) }
    val animateFontSize by animateIntAsState(
        targetValue = buttonState.fontSize,
        animationSpec = tween(durationMillis = 200),
        finishedListener = {
            buttonEnabled = true
            buttonState = KeyBoardTextButtonState.NONE
        }
    )

    Box(
        modifier = modifier
            .size(buttonWidth, buttonHeight),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier
                .clickable(
                    enabled = buttonEnabled,
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    buttonEnabled = false
                    onClickTextButton()
                    buttonState = KeyBoardTextButtonState.PRESSED
                },
            textAlign = TextAlign.Center,
            text = textButton.text,
            fontSize = animateFontSize.sp
        )
    }
}


@Composable
fun KeyboardImageButton(
    imageButton: KeyboardButtonType.ImageButton,
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
            painter = painterResource(id = imageButton.drawableRes),
            modifier = Modifier
                .size(animateImageSize.width.dp, animateImageSize.height.dp),
            contentDescription = null
        )
    }
}

/**
 * @param _emptyButton: 현재는 사용하지 않지만 통일성과 확장성을 위해 전달
 */
@Composable
fun KeyboardEmptyButton(
    _emptyButton: KeyboardButtonType.EmptyButton,
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
                (9 downTo 1).map { number ->
                    KeyboardButtonType.TextButton(number.toString())
                }
            )
            add(KeyboardButtonType.EmptyButton)
            add(KeyboardButtonType.TextButton("0"))
            add(KeyboardButtonType.ImageButton(R.drawable.ic_backspace))
        }.toList()
    }

    LazyVerticalGrid(
        cells = GridCells.Fixed(3),
        modifier = modifier.fillMaxSize()
    ) {
        items(buttons) { type ->
            when (type) {
                is KeyboardButtonType.TextButton -> {
                    KeyboardTextButton(
                        textButton = type,
                        onClickTextButton = { }
                    )
                }
                is KeyboardButtonType.ImageButton -> {
                    KeyboardImageButton(
                        imageButton = type,
                        onClickImageButton = { }
                    )
                }
                is KeyboardButtonType.EmptyButton -> {
                    KeyboardEmptyButton(_emptyButton = type)
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
            KeyboardButtonType.TextButton("1"),
            onClickTextButton = {}
        )
    }
}

@Preview
@Composable
fun KeyboardImageButtonPrev() {
    Surface {
        KeyboardImageButton(
            KeyboardButtonType.ImageButton(R.drawable.ic_backspace),
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
    data class ImageButton(@DrawableRes val drawableRes: Int) : KeyboardButtonType
    data class TextButton(val text: String) : KeyboardButtonType
    object EmptyButton : KeyboardButtonType
}

private val buttonWidth = 56.dp
private val buttonHeight = 48.dp