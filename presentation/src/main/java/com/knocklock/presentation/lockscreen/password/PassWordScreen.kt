package com.knocklock.presentation.lockscreen.password

import android.os.Build
import android.util.Log
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
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
import com.knocklock.presentation.lockscreen.password.Event.*
import com.knocklock.presentation.ui.theme.KnockLockTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @Created by 김현국 2023/01/09
 * @Time 11:58 AM
 */

@Composable
fun PassWordRoute(
    unLockPassWordScreen: () -> Unit,
    returnLockScreen: () -> Unit,
    passWordViewModel: PassWordViewModel = hiltViewModel(),
) {
    val eventState by passWordViewModel.eventState.collectAsStateWithLifecycle(NOTHING)
    val isPlaying = passWordViewModel.isPlaying
    val inputPassWordState = passWordViewModel.passWordState.toImmutableList()
    val passWordList = passWordViewModel.getPassWordList().toImmutableList()
    val scope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0F) }
    val view = LocalView.current
    LaunchedEffect(eventState) {
        when (eventState) {
            UNLOCK -> {
                unLockPassWordScreen()
            }
            RETURN -> {
                returnLockScreen()
            }
            VIBRATE -> {
                val TAG = "TwoToo PassWordRoute()"
                Log.d(TAG, "Vibrate")
                wigglePassword(offset = offsetX, coroutineScope = scope, view = view, resetEvent = {
                    passWordViewModel.resetState()
                })
            }
            else -> null
        }
    }
    PassWordScreen(
        isPlaying = isPlaying,
        inputPassWordState = inputPassWordState,
        passWordList = passWordList,
        removePassWord = passWordViewModel::removePassWord,
        updatePassWordState = passWordViewModel::updatePassWordState,
        offsetX = offsetX,
    )
}

@Composable
fun PassWordScreen(
    isPlaying: Boolean,
    inputPassWordState: ImmutableList<PassWord>,
    passWordList: ImmutableList<PassWord>,
    removePassWord: () -> Unit,
    updatePassWordState: (String) -> Unit,
    offsetX: Animatable<Float, AnimationVector1D>,
) {
    CirclePassWordBoard(
        modifier = Modifier.fillMaxSize(),
        passWordList = passWordList,
        onPassWordClick = updatePassWordState,
        removePassWord = removePassWord,
        isPlaying = isPlaying,
        offsetX = offsetX,
        inputPassWordState = inputPassWordState,
    )
}

private val shakeKeyFrames = keyframes {
    durationMillis = 800
    val easing = FastOutLinearInEasing
    for (i in 1..8) {
        val x = when (i % 3) {
            0 -> 4f
            1 -> -4f
            else -> 0f
        }
        x at durationMillis / 10 * i with easing
    }
}

private fun wigglePassword(
    offset: Animatable<Float, AnimationVector1D>,
    coroutineScope: CoroutineScope,
    resetEvent: () -> Unit,
    view: View? = null,
) {
    coroutineScope.launch {
        offset.animateTo(
            targetValue = 0f,
            animationSpec = shakeKeyFrames,
        )
    }.invokeOnCompletion {
        resetEvent()
    }
    view?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }
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
    modifier: Modifier = Modifier,
    inputPassWordState: ImmutableList<PassWord>,
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
    modifier: Modifier = Modifier,
    passWord: PassWord,
    onPassWordClick: (String) -> Unit,
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
            removePassWord = {},
            updatePassWordState = { },
            offsetX = remember { Animatable(0F) },
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
            removePassWord = {},
            updatePassWordState = { },
            offsetX = remember { Animatable(0F) },
        )
    }
}
