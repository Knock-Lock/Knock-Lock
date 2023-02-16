package com.knocklock.presentation.lockscreen.password

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.knocklock.presentation.R
import com.knocklock.presentation.ui.theme.KnockLockTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * @Created by 김현국 2023/01/09
 * @Time 11:58 AM
 */

@Composable
fun PassWordRoute(
    unLockPassWordScreen: () -> Unit,
    returnLockScreen: () -> Unit
) {
    PassWordScreen(
        unLockPassWordScreen = unLockPassWordScreen,
        returnLockScreen = returnLockScreen
    )
}

@Composable
fun PassWordScreen(
    unLockPassWordScreen: () -> Unit,
    returnLockScreen: () -> Unit
) {
    val passWordSpace = 50.dp
    val contentPadding = passWordSpace / 2
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp - passWordSpace * 3
    val circlePassWordNumberSize = screenWidthDp / 3

    val passWordScreenState = rememberPassWordScreenState(
        returnLockScreen = returnLockScreen,
        unLockPassWordScreen = unLockPassWordScreen
    )

    Column(
        modifier = Modifier.background(color = Color.Blue).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        Locker(
            modifier = Modifier.size(50.dp),
            isPlaying = passWordScreenState.isPlaying
        )
        InsertPassWordText()
        Spacer(modifier = Modifier.height(40.dp))
        InsertPassWordRow(
            modifier = Modifier.padding(horizontal = 50.dp).fillMaxWidth(),
            inputPassWordState = passWordScreenState.passWordState.toImmutableList()
        )
        Spacer(modifier = Modifier.height(100.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(contentPadding),
            verticalArrangement = Arrangement.spacedBy(passWordSpace),
            horizontalArrangement = Arrangement.spacedBy(passWordSpace)
        ) {
            items(items = passWordScreenState.getPassWordList()) { passWord: PassWord ->
                if (passWord.number.isEmpty()) {
                    CirclePassWordNumber(
                        modifier = Modifier.size(circlePassWordNumberSize),
                        passWord = passWord,
                        onPassWordClick = { }
                    )
                } else {
                    CirclePassWordNumber(
                        modifier = Modifier.size(circlePassWordNumberSize).background(
                            color = Color.LightGray.copy(alpha = 0.3f),
                            shape = CircleShape
                        ).clip(
                            CircleShape
                        ),
                        passWord = passWord,
                        onPassWordClick = passWordScreenState::updatePassWordState
                    )
                }
            }
        }
        BackButton(
            modifier = Modifier.align(Alignment.End).padding(end = contentPadding).size(circlePassWordNumberSize).clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) {
                passWordScreenState.removePassWord()
            }
        )
    }
}

@Composable
fun Locker(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.locker))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying
    )
    LottieAnimation(modifier = modifier, composition = composition, progress = { progress })
}

@Composable
fun InsertPassWordText() {
    Text(text = "암호 입력")
}

@Composable
fun InsertPassWordRow(
    modifier: Modifier = Modifier,
    inputPassWordState: ImmutableList<PassWord>
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 50.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        items(
            items = inputPassWordState
        ) { inputPassWord: PassWord ->
            InsertedPassWordCircle(inputPassWord = inputPassWord.number)
        }
    }
}

@Composable
fun InsertedPassWordCircle(
    inputPassWord: String
) {
    Canvas(modifier = Modifier.size(10.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawCircle(
            color = Color.White,
            center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
            radius = size.minDimension / 2,
            style = if (inputPassWord == "") Stroke(1.5f) else Fill
        )
    }
}

@Composable
fun CirclePassWordNumber(
    modifier: Modifier = Modifier,
    passWord: PassWord,
    onPassWordClick: (String) -> Unit
) {
    Column(
        modifier = modifier.clickable(enabled = passWord.number.isNotEmpty()) {
            onPassWordClick(passWord.number)
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = passWord.number,
            fontSize = 20.sp
        )
        Text(
            text = passWord.subText ?: ""
        )
    }
}

@Composable
fun BackButton(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "취소")
    }
}

@Preview
@Composable
fun PreviewLocker() {
    KnockLockTheme {
        Locker(isPlaying = false)
    }
}


@Preview
@Composable
fun PreviewCirclePassWordNumber() {
    KnockLockTheme {
        CirclePassWordNumber(
            passWord = PassWord.getPassWordList()[1],
            onPassWordClick = {}

        )
    }
}
