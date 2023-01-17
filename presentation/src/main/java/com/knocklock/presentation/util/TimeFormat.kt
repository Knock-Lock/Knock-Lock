package com.knocklock.presentation.util

abstract class TimeFormat {
    abstract val timeFormat: String
    val dateFormat: String = "MM월 dd일 E요일"
}

object TimeWithNoSecondFormat : TimeFormat() {
    override val timeFormat: String = "h:mm"
}

object TimeWithSecondFormat : TimeFormat() {
    override val timeFormat: String = "h:mm:ss"
}

object TimeVerticalFormat : TimeFormat() {
    override val timeFormat: String = "hh"
    const val minutesFormat: String = "mm"
}