import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.test.Test

/**
 * 时间测试
 *
 *@author AfeiBaili
 *@version 2026/1/15 17:44
 */

class LocalDateTimeTest {
    @Test
    fun localDateTimeTest() {
        val t1 = "16:2"

        val formatter1: DateTimeFormatter = DateTimeFormatter.ofPattern("H:m")
        val formatter2: DateTimeFormatter = DateTimeFormatter.ofPattern("H:m:s")
        val formatter5: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日-H:m")

        println(LocalTime.from(formatter1.parse(t1)))
    }
}