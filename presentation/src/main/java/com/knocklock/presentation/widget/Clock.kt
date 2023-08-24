package com.knocklock.presentation.widget

import android.graphics.Color
import android.widget.TextClock
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.knocklock.domain.model.TimeFormat
import java.util.*

@Composable
fun ClockWidget(
    timeFormat: TimeFormat,
    modifier: Modifier = Modifier,
    topTextClockSize: Float = 80F,
    bottomTextClockSize: Float = 21F,

) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AndroidView(factory = { context ->
            TextClock(context).apply {
                format12Hour =
                    if (Locale.getDefault().language.equals("ko")) {
                        TimeFormat.DATE_FORMAT_KOR
                    } else {
                        TimeFormat.DATE_FORMAT_ENG
                    }
                textSize = bottomTextClockSize
                setTextColor(Color.WHITE)
            }
        }, update = { textClock ->
            textClock.format12Hour =
                if (Locale.getDefault().language.equals("ko")) {
                    TimeFormat.DATE_FORMAT_KOR
                } else {
                    TimeFormat.DATE_FORMAT_ENG
                }
        })
        AndroidView(factory = { context ->
            TextClock(context).apply {
                format12Hour = timeFormat.timeFormat
                textSize = topTextClockSize
                setTextColor(Color.WHITE)
            }
        }, update = { textClock ->
            textClock.format12Hour = timeFormat.timeFormat
        })
    }
}

@Preview
@Composable
private fun PreviewClockOne() {
    ClockWidget(timeFormat = TimeFormat.TimeWithNoSecondFormat)
}

@Preview
@Composable
private fun PreviewClockTwo() {
    ClockWidget(timeFormat = TimeFormat.TimeWithSecondFormat)
}

@Preview
@Composable
private fun PreviewClockThird() {
    ClockWidget(timeFormat = TimeFormat.TimeVerticalFormat)
}

@Preview
@Composable
private fun PreviewClockFourth() {
    ClockWidget(timeFormat = TimeFormat.TimeWithMeridiem)
}
