package online.afeibaili.module.todo

import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class Todo(
    val uuid: String,
    val at: Long,
    val message: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val dateTime: LocalDateTime?,
) {

    override fun toString(): String {
        return buildString {
            appendLine("ID: $uuid")
            appendLine("事件: $message")
            if (dateTime != null) {
                appendLine("时间: ${LocalDateTimeSerializer.formatter.format(dateTime)}")
            }
            appendLine()
        }
    }
}