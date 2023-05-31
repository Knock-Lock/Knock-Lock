package com.knocklock.presentation.lockscreen.password

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import com.knocklock.presentation.lockscreen.password.Event.NOTHING
import com.knocklock.presentation.lockscreen.password.Event.RETURN
import com.knocklock.presentation.lockscreen.password.Event.UNLOCK
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
    returnLockScreen: () -> Unit,
    passWordViewModel: PassWordViewModel = hiltViewModel(),
) {
    val eventState by passWordViewModel.eventState.collectAsStateWithLifecycle(NOTHING)
    val isPlaying = passWordViewModel.isPlaying
    val inputPassWordState = passWordViewModel.passWordState.toImmutableList()
    val passWordList = passWordViewModel.getPassWordList().toImmutableList()
    LaunchedEffect(eventState) {
        when (eventState) {
            UNLOCK -> {
                unLockPassWordScreen()
            }
            RETURN -> {
                returnLockScreen()
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
    )
}

@Composable
fun PassWordScreen(
    isPlaying: Boolean,
    inputPassWordState: ImmutableList<PassWord>,
    passWordList: ImmutableList<PassWord>,
    removePassWord: () -> Unit,
    updatePassWordState: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(50.dp))
            Locker(
                modifier = Modifier.size(50.dp),
                isPlaying = isPlaying,
            )
            InsertPassWordText()
            Spacer(modifier = Modifier.height(40.dp))
            InsertPassWordRow(
                modifier = Modifier.padding(horizontal = 50.dp).fillMaxWidth(),
                inputPassWordState = inputPassWordState,
            )
        }
        CirclePassWordBoard(
            modifier = Modifier.fillMaxSize(),
            passWordList = passWordList,
            onPassWordClick = updatePassWordState,
            removePassWord = removePassWord,
        )
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

@Preview
@Composable
private fun PreviewLocker() {
    KnockLockTheme {
        Locker(isPlaying = false)
    }
}

@Preview
@Composable
private fun PreviewCirclePassWordNumber() {
    KnockLockTheme {
        CirclePassWordNumber(
            passWord = PassWord.getPassWordList()[1],
            onPassWordClick = {},

        )
    }
}

@Preview(widthDp = 600, heightDp = 900)
@Composable
private fun PreviewPassWordScreen() {
    KnockLockTheme {
        PassWordScreen(
            isPlaying = false,
            inputPassWordState = PassWord.getPassWordList().take(6).toImmutableList(),
            passWordList = PassWord.getPassWordList().toImmutableList(),
            removePassWord = {},
            updatePassWordState = { },
        )
    }
}

@Preview(widthDp = 840, heightDp = 2080)
@Composable
private fun PreviewPassWordScreen2() {
    KnockLockTheme {
        PassWordScreen(
            isPlaying = false,
            inputPassWordState = PassWord.getPassWordList().take(6).toImmutableList(),
            passWordList = PassWord.getPassWordList().toImmutableList(),
            removePassWord = {},
            updatePassWordState = { },
        )
    }
}
