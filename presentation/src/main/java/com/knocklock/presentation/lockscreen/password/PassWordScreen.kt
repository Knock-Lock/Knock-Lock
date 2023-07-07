package com.knocklock.presentation.lockscreen.password

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.knocklock.presentation.R
import com.knocklock.presentation.lockscreen.password.Event.Nothing
import com.knocklock.presentation.lockscreen.password.Event.Return
import com.knocklock.presentation.lockscreen.password.Event.Unlock
import com.knocklock.presentation.lockscreen.password.Event.Vibrate
import com.knocklock.presentation.ui.theme.KnockLockTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * @Created by 김현국 2023/01/09
 * @Time 11:58 AM
 */

@Composable
fun PassWordRoute(
    onPassWordScreenUnLock: () -> Unit,
    returnLockScreen: () -> Unit,
    passWordViewModel: PassWordViewModel = hiltViewModel(),
) {
    val eventState by passWordViewModel.eventState.collectAsStateWithLifecycle(Nothing)
    val isPlaying = passWordViewModel.isPlaying
    val inputPassWordState = passWordViewModel.passWordState.toImmutableList()
    val passWordList = passWordViewModel.getPassWordList().toImmutableList()
    LaunchedEffect(eventState) {
        when (eventState) {
            Unlock -> {
                onPassWordScreenUnLock()
            }
            Return -> {
                returnLockScreen()
            }
            else -> null
        }
    }
    PassWordScreen(
        isPlaying = isPlaying,
        inputPassWordState = inputPassWordState,
        passWordList = passWordList,
        onPassWordRemove = passWordViewModel::removePassWord,
        onPassWordStateUpdate = passWordViewModel::updatePassWordState,
        eventState = eventState,
        onWiggleAnimationEnd = {
            passWordViewModel.resetState()
        }
    )
}

@Composable
fun PassWordScreen(
    isPlaying: Boolean,
    inputPassWordState: ImmutableList<PassWord>,
    passWordList: ImmutableList<PassWord>,
    onPassWordRemove: () -> Unit,
    onPassWordStateUpdate: (String) -> Unit,
    onWiggleAnimationEnd: () -> Unit,
    eventState: Event,
) {
    CirclePassWordBoard(
        modifier = Modifier.fillMaxSize(),
        passWordList = passWordList,
        onPassWordClick = onPassWordStateUpdate,
        onPassWordRemove = onPassWordRemove,
        isPlaying = isPlaying,
        inputPassWordState = inputPassWordState,
        eventState = eventState,
        onWiggleAnimationEnd = onWiggleAnimationEnd
    )
}

@Composable
fun Locker(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.locker))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
    )
    LottieAnimation(modifier = modifier, composition = composition, progress = { progress })
}

@Composable
fun InsertPassWordText() {
    Text(text = "암호 입력")
}

@Composable
fun InsertPassWordRow(
    inputPassWordState: ImmutableList<PassWord>,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        items(
            items = inputPassWordState,
        ) { inputPassWord: PassWord ->
            InsertedPassWordCircle(inputPassWord = inputPassWord.number)
        }
    }
}

@Composable
fun InsertedPassWordCircle(
    inputPassWord: String,
) {
    Canvas(modifier = Modifier.size(10.dp)) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawCircle(
            color = Color.White,
            center = Offset(x = canvasWidth / 2, y = canvasHeight / 2),
            radius = size.minDimension / 2,
            style = if (inputPassWord == "") Stroke(1.5f) else Fill,
        )
    }
}

@Composable
fun CirclePassWordNumber(
    passWord: PassWord,
    onPassWordClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(enabled = passWord.number.isNotEmpty()) {
            onPassWordClick(passWord.number)
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = passWord.number,
            fontSize = 20.sp,
        )
        Text(
            text = passWord.subText ?: "",
        )
    }
}

@Composable
fun BackButton(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(text = "취소")
    }
}

@Preview(widthDp = 360, heightDp = 640, showSystemUi = false, showBackground = true)
@Composable
private fun PreviewPassWordScreen360640() {
    KnockLockTheme {
        PassWordScreen(
            isPlaying = false,
            inputPassWordState = PassWord.getPassWordList().take(6).toImmutableList(),
            passWordList = PassWord.getPassWordList().toImmutableList(),
            onPassWordRemove = {},
            onPassWordStateUpdate = { },
            eventState = Vibrate,
            onWiggleAnimationEnd = {}
        )
    }
}

@Preview(widthDp = 480, heightDp = 800, showSystemUi = false, showBackground = true)
@Composable
private fun PreviewPassWordScreen480800() {
    KnockLockTheme {
        PassWordScreen(
            isPlaying = false,
            inputPassWordState = PassWord.getPassWordList().take(6).toImmutableList(),
            passWordList = PassWord.getPassWordList().toImmutableList(),
            onPassWordRemove = {},
            onPassWordStateUpdate = { },
            eventState = Vibrate,
            onWiggleAnimationEnd = {}
        )
    }
}
