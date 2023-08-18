package com.knocklock.presentation.lockscreen.util

import com.knocklock.domain.model.TimeFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private object TIME_MAXIMUM {
    const val SEC = 60
    const val MIN = 60
    const val HOUR = 24
    const val DAY = 7
}

fun formatTimeString(regTime: Long): String {
    val curTime = System.currentTimeMillis()
    var diffTime = (curTime - regTime) / 1000
    val msg = if (diffTime < TIME_MAXIMUM.SEC) {
        "방금 전"
    } else if (TIME_MAXIMUM.SEC.let { diffTime /= it; diffTime } < TIME_MAXIMUM.MIN) {
        diffTime.toString() + "분 전"
    } else if (TIME_MAXIMUM.MIN.let { diffTime /= it; diffTime } < TIME_MAXIMUM.HOUR) {
        diffTime.toString() + "시간 전"
    } else if (TIME_MAXIMUM.HOUR.let { diffTime /= it; diffTime } < TIME_MAXIMUM.DAY) {
        diffTime.toString() + "일 전"
    } else {
        val date = Date(regTime)
        try {
            SimpleDateFormat(TimeFormat.TimeWithMeridiem.timeFormat, Locale.KOREA).format(date)
        } catch (e: Exception) {
            ""
        }
    }
    return msg
}
