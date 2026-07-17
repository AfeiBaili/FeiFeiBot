package online.afeibaili.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Time {
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd-HH:mm")
    fun formatMonthDayTime(ldt: LocalDateTime): String = ldt.format(dateTimeFormatter)
    fun now(): LocalDateTime = LocalDateTime.now()
}