package com.knocklock.presentation.setting.credit

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.knocklock.presentation.R
import kotlinx.coroutines.delay


enum class Developer(
    @StringRes val nameRes: Int,
    @DrawableRes val profileRes: Int,
    @StringRes val commentRes: Int
) {
    HYUN_KUK(R.string.daq, R.drawable.hyunkuk, R.string.daq_comment),
    HYUN_SU(R.string.hence, R.drawable.hyunsu, R.string.hence_comment),
    MINUK(R.string.jaeryo, R.drawable.minuk, R.string.jaeryo_comment);
}

@Composable
fun DeveloperItem(
    modifier: Modifier = Modifier,
    developer: Developer,
    delay: Int
) {
    var imageVisible by remember { mutableStateOf(false) }
    var textVisible by remember { mutableStateOf(false) }
    val duration = 1200

    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.CenterVertically)
            .padding(start = 10.dp, end = 10.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start,
    ) {
        AnimatedVisibility(
            visible = imageVisible,
            enter = fadeIn(animationSpec = tween(durationMillis = duration, delayMillis = delay))
        ) {
            Image(
                modifier = modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(16.dp)),
                painter = painterResource(id = developer.profileRes),
                contentDescription = null
            )
        }
        AnimatedVisibility(
            visible = textVisible,
            enter = slideInHorizontally(
                animationSpec = tween(durationMillis = duration, delayMillis = delay),
                initialOffsetX = { it }
            ) + fadeIn(
                animationSpec = tween(durationMillis = duration, delayMillis = delay)
            )
        ) {
            Column(
                modifier = modifier
                    .padding(start = 10.dp, bottom = 10.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = stringResource(id = developer.nameRes),
                    fontSize = 22.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = modifier.padding(2.dp))
                Text(
                    text = stringResource(id = developer.commentRes),
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 15.sp,
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(10)
        imageVisible = true
        textVisible = true
    }
}

@Preview
@Composable
private fun PreviewDeveloper() {
    DeveloperItem(
        developer = Developer.HYUN_KUK,
        delay = 500
    )
}