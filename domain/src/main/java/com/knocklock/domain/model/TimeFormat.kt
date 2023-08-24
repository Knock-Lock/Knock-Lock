package com.knocklock.domain.model

enum class TimeFormat(val timeFormat: String) {
    TimeWithNoSecondFormat("hh:mm"),
    TimeWithSecondFormat("hh:mm:ss"),
    TimeVerticalFormat("hh\nmm"),
    TimeWithMeridiem("a HH:mm")
    ;
    companion object {
        val DEFAULT_TIME_FORMAT = TimeWithNoSecondFormat

        const val DATE_FORMAT_KOR: String = "MMM d일 EEEE"
        const val DATE_FORMAT_ENG: String = "E, MMM d"

        fun getTimeFormat(format: String): TimeFormat {
            return TimeFormat.values().find { it.name == format } ?: DEFAULT_TIME_FORMAT
        }
    }
}
