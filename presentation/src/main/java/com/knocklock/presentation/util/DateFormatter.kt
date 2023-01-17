package com.knocklock.presentation.util

import java.text.SimpleDateFormat
import java.util.*

const val timeFormat = "h:mm"
const val timeFormatWithSecond = "h:mm:ss"

const val dateFormatKor = "MM월 dd일 E요일"
const val dateFormatEng = "EEE dd MMM"

fun getNormalTimeFormat(date: Date): String {
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
    val dateFormat = SimpleDateFormat(dateFormatEng, Locale.ENGLISH)
    return dateFormat.format(date)
}

interface TimeFormat {
    fun getDateFormat(date: Date): String
    fun getTimeFormat(date: Date): String
}

object KoreaFormat : TimeFormat {
    override fun getDateFormat(date: Date) = getKoreanDateFormat(date)

    override fun getTimeFormat(date: Date) = getTimeWithSecondFormat(date)
}

object KoreaSecondFormat : TimeFormat {
    override fun getDateFormat(date: Date) = getKoreanDateFormat(date)

    override fun getTimeFormat(date: Date) = getTimeWithSecondFormat(date)
}

object EnglishFormat : TimeFormat {
    override fun getDateFormat(date: Date) = getEnglishDateFormat(date)

    override fun getTimeFormat(date: Date) = getNormalTimeFormat(date)
}

object EnglishSecondFormat : TimeFormat {
    override fun getDateFormat(date: Date) = getEnglishDateFormat(date)

    override fun getTimeFormat(date: Date) = getTimeWithSecondFormat(date)
}