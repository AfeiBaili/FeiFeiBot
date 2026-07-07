package online.afeibaili.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


/**
 * # 时间解析器
 *
 * @author AfeiBaili
 * @version 2026/7/7 21:57
 */

object TimeParser {
    class TimerParserException(msg: String) : RuntimeException(msg)


    const val SEPARATOR = "-"
    val formatterList = listOf(
        DateTimeFormatter.ofPattern("H:m"),
        DateTimeFormatter.ofPattern("H.m"),
        DateTimeFormatter.ofPattern("H：m"),
        DateTimeFormatter.ofPattern("H:m:s"),
        DateTimeFormatter.ofPattern("yyyy年M月d日${SEPARATOR}H:m"),
        DateTimeFormatter.ofPattern("yyyy年M月d日${SEPARATOR}H:m:s"),
    )

    val dateTimeToStringFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日${SEPARATOR}H:m:s")

    fun LocalDateTime.toDateTimeString(): String {
        return this.format(dateTimeToStringFormatter)
    }

    fun parse(formatString: String): LocalDateTime {
        return if (formatString.startsWith("+")) parseAddDateTime(formatString)
        else parseDateTime(formatString, 0)
    }

    fun parseIsInfinite(formatString: String): Boolean {
        return formatString == "+无限"
    }

    fun parseDateTime(formatString: String, formatterIndex: Int): LocalDateTime {
        if (formatterIndex == formatterList.size)
            throw TimerParserException("无法格式化时间，不支持的格式: $formatString")
        val formatter: DateTimeFormatter = formatterList[formatterIndex]
        val localDateTime = runCatching {
            val split: List<String> = formatString.split(SEPARATOR)
            return@runCatching if (split.size == 1) {
                LocalDateTime.of(LocalDate.now(), LocalTime.from(formatter.parse(split[0])))
            } else {
                LocalDateTime.from(formatter.parse(formatString))
            }
        }.getOrElse { null }

        return localDateTime ?: parseDateTime(formatString, formatterIndex + 1)
    }

    fun parseAddDateTime(formatString: String): LocalDateTime {
        val dateStr = formatString.removePrefix("+")
        val dateChar: Char = dateStr.last()
        val date = runCatching {
            dateStr.removeSuffix(dateChar.toString()).toInt()
        }.getOrElse { throw TimerParserException("无法格式化时间，请格式化数字") }

        val now: LocalDateTime = LocalDateTime.now()

        val localDateTime: LocalDateTime = when (dateChar) {
            's' -> now.plusSeconds(date.toLong())
            'm' -> now.plusMinutes(date.toLong())
            'h' -> now.plusHours(date.toLong())
            'd' -> now.plusDays(date.toLong())

            else -> throw TimerParserException("未知的解析格式，目前支持: $formatString")
        }

        return localDateTime
    }

    fun toString(withStartString: String): String {
        return "$withStartString:\n${toString()}"
    }

    override fun toString(): String {
        return """
                时间日期格式化格式：
                1.根据添加时间解析
                +10s
                +30m
                +12h
                +30d
                2.根据时间日期解析
                20:00
                20.00
                20：00
                20:00:10
                2005年05月16日-20:00
                2005年05月16日-20:00:00
            """.trimIndent()
    }
}