import online.afeibaili.util.TimeParser
import kotlin.test.Test

/**
 * 测试时间
 *
 * @author AfeiBaili
 * @version 2026/7/7 22:54
 */

class TestDateTime {
    @Test
    fun test1() {
        val fs = "20.00"
        println(TimeParser)
        println("==================")
        println(TimeParser.parse(fs))
    }
}