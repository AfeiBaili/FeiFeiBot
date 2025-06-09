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
}