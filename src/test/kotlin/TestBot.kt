import online.afeibaili.file.getJsonPropertyByResource
import kotlin.test.Test

class TestBot {
    @Test
    fun testResourceFile() {
        val string: String = getJsonPropertyByResource(TestBot::class.java, "config.json", "version")
        println(string)
    }
}