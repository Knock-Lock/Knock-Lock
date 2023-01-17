package com.knocklock.presentation.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.knocklock.presentation.util.*
import java.util.*


@Composable
fun ClockWidget(
    modifier: Modifier = Modifier,
    timeFormat: TimeFormat,
    currentTime: Date
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = timeFormat.getTimeFormat(currentTime),
            color = Color.White,
            fontSize = 36.sp,
        )
        Text(
            text = timeFormat.getDateFormat(currentTime),
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Preview
@Composable
fun PreviewClockOne() {
    ClockWidget(
        timeFormat = KoreaFormat,
        currentTime = Date(System.currentTimeMillis())
    )
}

@Preview
@Composable
fun PreviewClockTwo() {
    ClockWidget(
        timeFormat = KoreaSecondFormat,
        currentTime = Date(System.currentTimeMillis())
    )
}

@Preview
@Composable
fun PreviewClockThree() {
    ClockWidget(
        timeFormat = EnglishFormat,
        currentTime = Date(System.currentTimeMillis())
    )
}

@Preview
@Composable
fun PreviewClockFour() {
    ClockWidget(
        timeFormat = EnglishSecondFormat,
        currentTime = Date(System.currentTimeMillis())
    )
}