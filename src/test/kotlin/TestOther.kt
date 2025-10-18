import com.fasterxml.jackson.databind.ObjectMapper
import online.afeibaili.command.Commands
import online.afeibaili.module.echo.cave.Data
import online.afeibaili.module.echo.cave.EchoCave
import java.awt.List
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import kotlin.test.Test

class TestOther {
    @Test
    fun test() {
        val text = "20:00"

        val parse: TemporalAccessor = DateTimeFormatter.ofPattern("HH:mm").parse(text)
        val time: LocalTime = LocalTime.from(parse)
        println(
            LocalDateTime.now().withHour(time.hour).withMinute(time.minute).withSecond(time.second)

        )
    }

    @Test
    fun test2() {
        val data: kotlin.collections.List<Data> = listOf<Data>(
            Data("message,", 12313L, "hh"),
            Data("message,", 12313L, "hh"),
            Data("message,", 12313L, "hh"),
        )

        println(ObjectMapper().writeValueAsString(data))
    }
    @Test
    fun test3(){
        println(LocalDateTime.now())
    }
}