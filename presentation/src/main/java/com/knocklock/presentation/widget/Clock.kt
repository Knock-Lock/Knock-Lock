package com.knocklock.presentation.widget

import android.graphics.Color
import android.widget.TextClock
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.knocklock.presentation.util.TimeFormat
import com.knocklock.presentation.util.TimeVerticalFormat
import com.knocklock.presentation.util.TimeWithNoSecondFormat
import com.knocklock.presentation.util.TimeWithSecondFormat
import java.util.*


@Composable
fun ClockWidget(
    modifier: Modifier = Modifier,
    timeFormat: TimeFormat,
) {
    Column(
        modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(factory = { context ->
            TextClock(context).apply {
                format12Hour = timeFormat.timeFormat
                textSize = 36F
                setTextColor(Color.WHITE)
            }
        }, update = { textClock ->
            textClock.format12Hour = timeFormat.timeFormat
        })
        if (timeFormat is TimeVerticalFormat) {
            AndroidView(factory = { context ->
                TextClock(context).apply {
                    format12Hour = timeFormat.minutesFormat
                    textSize = 36F
                    setTextColor(Color.WHITE)
                }
            }, update = { textClock ->
                textClock.format12Hour = timeFormat.minutesFormat
            })
        }
        AndroidView(factory = { context ->
            TextClock(context).apply {
                format12Hour =
                    if (Locale.getDefault().language.equals("ko")) {
                        timeFormat.dateFormatKor
                    } else {
                        timeFormat.dateFormatEng
                    }
                textSize = 12F
                setTextColor(Color.WHITE)
            }
        }, update = { textClock ->
            textClock.format12Hour =
                if (Locale.getDefault().language.equals("ko")) {
                    timeFormat.dateFormatKor
                } else {
                    timeFormat.dateFormatEng
                }
        })
    }
}

@Preview
@Composable
fun PreviewClockOne() {
    ClockWidget(timeFormat = TimeWithNoSecondFormat)
}

@Preview
@Composable
fun PreviewClockTwo() {
    ClockWidget(timeFormat = TimeWithSecondFormat)
}

@Preview
@Composable
fun PreviewClockThird() {
    ClockWidget(timeFormat = TimeVerticalFormat)
}