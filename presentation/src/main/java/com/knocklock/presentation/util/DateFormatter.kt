package com.knocklock.presentation.util

import java.text.SimpleDateFormat
import java.util.*

const val timeFormat = "h:mm"
const val timeFormatWithSecond = "h:mm:ss"

const val dateFormatKor = "MM월 dd일 E요일"
const val dateFormatEng = "EEE dd MMM"

fun getTimeFormat(date: Date): String {
    val dateFormat = SimpleDateFormat(timeFormat, Locale.KOREAN)
    return dateFormat.format(date)
}

fun getTimeWithSecondFormat(date: Date): String {
    val dateFormat = SimpleDateFormat(timeFormatWithSecond, Locale.KOREAN)
    return dateFormat.format(date)
}

fun getKoreanDateFormat(date: Date): String {
    val dateFormat = SimpleDateFormat(dateFormatKor, Locale.KOREAN)
    return dateFormat.format(date)
}

fun getEnglishDateFormat(date: Date): String {
    val dateFormat = SimpleDateFormat(dateFormatEng, Locale.KOREAN)
    return dateFormat.format(date)
}