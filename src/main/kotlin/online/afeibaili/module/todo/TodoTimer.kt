package online.afeibaili.module.todo

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date
import java.util.Timer
import java.util.TimerTask
import kotlin.concurrent.schedule

object TodoTimer {
    val todoTimer = Timer()

    fun createTask(dateTime: LocalDateTime, action: TimerTask.() -> Unit) {
        val date: Date = Date.from(dateTime.toInstant(ZoneOffset.ofHours(8)))
        todoTimer.schedule(date, action)
    }

    fun cancelTimer() {
        todoTimer.cancel()
    }
}