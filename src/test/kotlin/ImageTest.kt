import online.afeibaili.bot.Kolors
import online.afeibaili.bot.json.ImageResponse
import online.afeibaili.config
import online.afeibaili.file.ConfigFile
import kotlin.test.Test

class ImageTest {
    @Test
    fun testImage() {
        val configFile = ConfigFile()
        config = configFile.config

        val kolors = Kolors()
        val response: ImageResponse = kolors.send("猫", 4)
        response.images.forEach { println(it) }
    }
}