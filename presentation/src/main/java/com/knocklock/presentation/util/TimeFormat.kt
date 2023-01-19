package com.knocklock.presentation.util

abstract class TimeFormat {
    abstract val timeFormat: String
    val dateFormatKor: String = "MMM d일 EEEE"
    val dateFormatEng: String = "E, MMM d"
}

object TimeWithNoSecondFormat : TimeFormat() {
    override val timeFormat: String = "h:m"
}

object TimeWithSecondFormat : TimeFormat() {
    override val timeFormat: String = "h:m:s"
}

object TimeVerticalFormat : TimeFormat() {
    override val timeFormat: String = "hh"
    const val minutesFormat: String = "mm"
}