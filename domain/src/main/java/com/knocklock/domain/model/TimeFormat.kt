package com.knocklock.domain.model

enum class TimeFormat(val timeFormat: String) {
    TimeWithNoSecondFormat("h:m"),
    TimeWithSecondFormat("h:m:s"),
    TimeVerticalFormat("hh\nmm"),
    TimeWithMeridiem("a HH:mm")
    ;
    companion object {
        const val dateFormatKor: String = "MMM d일 EEEE"
        const val dateFormatEng: String = "E, MMM d"
    }
}
