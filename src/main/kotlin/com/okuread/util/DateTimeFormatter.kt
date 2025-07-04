package com.okuread.util

import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun String.toLocalDate(formatter: DateTimeFormatter = DateTimeFormatter.RFC_1123_DATE_TIME): LocalDate {
    // Parse the date string as ZonedDateTime
    val zonedDateTime = ZonedDateTime.parse(this, formatter)

    // Extract the LocalDate
    return zonedDateTime.toLocalDate()
}